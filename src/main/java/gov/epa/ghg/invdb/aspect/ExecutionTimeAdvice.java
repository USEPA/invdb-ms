package gov.epa.ghg.invdb.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
public class ExecutionTimeAdvice {

	@Around("@annotation(gov.epa.ghg.invdb.aspect.ExecutionTime)")
	public Object executionTime(ProceedingJoinPoint point) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object object = point.proceed();
		long endtime = System.currentTimeMillis();
		log.info(point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName()
				+ "(...) executed in " + (endtime - startTime) + "ms");
		return object;
	}
}