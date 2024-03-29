package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents company data transfer object
 */
public record CompanyDto(
        Long id,
        String name,
        AddressDto address,
        String organisationId) {
}
