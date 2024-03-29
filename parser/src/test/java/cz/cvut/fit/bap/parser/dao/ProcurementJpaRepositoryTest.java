package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.Procurement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
class ProcurementJpaRepositoryTest{
    @Autowired
    private ProcurementJpaRepository procurementJpaRepository;

    @Test
    void existBySystemNumber(){
        String systemNumber = "systemNumber";
        Procurement procurement = new Procurement();
        procurement.setSystemNumber(systemNumber);
        assertFalse(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber));
        procurementJpaRepository.save(procurement);
        assertTrue(procurementJpaRepository.existsProcurementBySystemNumber(systemNumber));
    }
}