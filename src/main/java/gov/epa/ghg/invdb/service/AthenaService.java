package gov.epa.ghg.invdb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.ColumnInfo;
import software.amazon.awssdk.services.athena.model.Datum;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

@Service
@Log4j2
public class AthenaService {
    @Autowired
    private AwsAuthService authService;

    // based on
    // https://docs.aws.amazon.com/athena/latest/ug/start-query-execution.html
    public ArrayList<Map<String, String>> runQuery(String query) throws Exception {
        ArrayList<Map<String, String>> endResults = new ArrayList<Map<String, String>>();
        StaticCredentialsProvider credentialsProvider = this.authService.getCredentials();
        try (AthenaClient athenaClient = AthenaClient.builder().credentialsProvider(credentialsProvider).build()) {
            String queryExecutionId = submitAthenaQuery(athenaClient, query);
            waitForQueryToComplete(athenaClient, queryExecutionId);
            endResults = processResultRows(athenaClient, queryExecutionId);
            athenaClient.close();
        }
        return endResults;
    }

    // Submits a sample query to Amazon Athena and returns the execution ID of the
    // query.
    private String submitAthenaQuery(AthenaClient athenaClient, String query) {
        try {
            // The QueryExecutionContext allows us to set the database.
            QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                    .database("invdb")
                    .build();

            String outputBucket = System.getenv("ATHENA_OUTPUT_BUCKET");
            if (outputBucket == null) {
                outputBucket = "s3://invdb-athenaquery-temp-files/query-results/";
            }
            // The result configuration specifies where the results of the query should go.
            ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                    .outputLocation(outputBucket)
                    .build();

            StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                    .queryString(query)
                    .queryExecutionContext(queryExecutionContext)
                    .resultConfiguration(resultConfiguration)
                    .build();

            StartQueryExecutionResponse startQueryExecutionResponse = athenaClient
                    .startQueryExecution(startQueryExecutionRequest);
            return startQueryExecutionResponse.queryExecutionId();

        } catch (AthenaException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId)
            throws InterruptedException {
        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId)
                .build();

        GetQueryExecutionResponse getQueryExecutionResponse;
        boolean isQueryStillRunning = true;
        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            String queryState = getQueryExecutionResponse.queryExecution().status().state().toString();
            if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                throw new RuntimeException(
                        "The Amazon Athena query failed to run with error message: " + getQueryExecutionResponse
                                .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                throw new RuntimeException("The Amazon Athena query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                isQueryStillRunning = false;
            } else {
                // Sleep an amount of time before retrying again.
                Thread.sleep(1000);
            }
            System.out.println("The current status is: " + queryState);
        }
    }

    // This code retrieves the results of a query
    private ArrayList<Map<String, String>> processResultRows(AthenaClient athenaClient, String queryExecutionId) {
        ArrayList<Map<String, String>> endResults = new ArrayList<Map<String, String>>();
        try {
            // Max Results can be set but if its not set,
            // it will choose the maximum page size.
            GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                    .queryExecutionId(queryExecutionId)
                    .build();

            GetQueryResultsIterable getQueryResultsResults = athenaClient
                    .getQueryResultsPaginator(getQueryResultsRequest);
            for (GetQueryResultsResponse result : getQueryResultsResults) {
                List<ColumnInfo> columnInfoList = result.resultSet().resultSetMetadata().columnInfo();
                List<Row> results = result.resultSet().rows();
                endResults = processRow(results, columnInfoList);
            }
            return endResults;
        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
            return new ArrayList<Map<String, String>>();
        }
    }

    private ArrayList<Map<String, String>> processRow(List<Row> rows, List<ColumnInfo> columnInfoList) {
        ArrayList<Map<String, String>> results = new ArrayList<Map<String, String>>();
        List<String> columns = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfoList) {
            columns.add(columnInfo.name());
        }
        for (Row row : rows) {
            int index = 0;
            Map<String, String> result = new HashMap<String, String>();
            for (Datum datum : row.data()) {
                result.put(columns.get(index), datum.varCharValue());
                index++;
            }
            results.add(result);
        }
        return results;
    }
}
