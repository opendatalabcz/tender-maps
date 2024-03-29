package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
class AddressJpaRepositoryTest{
    @Autowired
    private AddressJpaRepository addressJpaRepository;

    @Test
    void testReadAddress(){
        final Address address = new Address("SK", "65", "Bratislava", "Bratislavska",
                "16000", "1", null, null);
        assertTrue(addressJpaRepository.readAddress(address).isEmpty());

        addressJpaRepository.save(address);

        Optional<Address> returnedAddress = addressJpaRepository.readAddress(address);
        assertTrue(returnedAddress.isPresent());

        Assertions.assertEquals(address.getCountryCode(), returnedAddress.get().getCountryCode());
        Assertions.assertEquals(address.getCity(), returnedAddress.get().getCity());
        Assertions.assertEquals(address.getPostalCode(), returnedAddress.get().getPostalCode());
        Assertions.assertEquals(address.getStreet(), returnedAddress.get().getStreet());
        Assertions.assertEquals(address.getBuildingNumber(),
                returnedAddress.get().getBuildingNumber());
    }

    @Test
    void nullValuesInAddressExisting(){
        final Address address = new Address("SK", null, "Bratislava", null,
                "16000", "1", null, null);

        assertTrue(addressJpaRepository.readAddress(address).isEmpty());

        addressJpaRepository.save(address);

        Optional<Address> returnedAddress = addressJpaRepository.readAddress(address);
        assertTrue(returnedAddress.isPresent());

        Assertions.assertEquals(address.getCountryCode(), returnedAddress.get().getCountryCode());
        Assertions.assertEquals(address.getCity(), returnedAddress.get().getCity());
        Assertions.assertEquals(address.getPostalCode(), returnedAddress.get().getPostalCode());
        Assertions.assertEquals(address.getStreet(), returnedAddress.get().getStreet());
        Assertions.assertEquals(address.getBuildingNumber(),
                returnedAddress.get().getBuildingNumber());
    }
}