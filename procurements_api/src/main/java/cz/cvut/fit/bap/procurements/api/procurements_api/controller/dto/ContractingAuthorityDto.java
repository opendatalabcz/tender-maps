package cz.cvut.fit.bap.procurements.api.procurements_api.controller.dto;

/**
 * Class represents contracting authority data transfer object
 */
public record ContractingAuthorityDto(
        Long id,
        String name,
        AddressDto address,
        String url) {
}
