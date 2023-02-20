package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.CompanyJpaRepository;
import cz.cvut.fit.bap.parser.domain.Company;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService extends AbstractCreateService<Company, Long>{
    public CompanyService(CompanyJpaRepository repository){
        super(repository);
    }

    @Override
    public Company create(Company entity){
        Optional<Company> company = readByName(entity.getName());
        return company.orElseGet(() -> super.create(entity));
    }

    public Optional<Company> readByName(String name){
        return ((CompanyJpaRepository) repository).findCompanyByName(name);
    }
}
