package gov.epa.ghg.invdb.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseDimTableRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    void handleDimfieldValueChange(String currentValue, String newValue, Integer userId);
}