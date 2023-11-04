package cz.cvut.fit.bap.parser.controller.scrapper;

import cz.cvut.fit.bap.parser.controller.data.AddressData;
import cz.cvut.fit.bap.parser.controller.data.OfferDetailPageData;
import org.jsoup.nodes.Document;

import java.math.BigDecimal;

public class OfferDetailScrapper extends AbstractScrapper {
    public OfferDetailScrapper(Document document) {
        super(document);
    }

    /**
     * Gets all data from offer detail page.
     *
     * @return data form offer detail page
     */
    public OfferDetailPageData getPageData() {
        return new OfferDetailPageData(getCompanyAddress(),
                getPriceVAT(),
                getOrganisationId(),
                getVATIdNumber(),
                getIsRejectedDueTooLow(),
                getIsWithdrawn(),
                getIsAssociationOfSuppliers());
    }

    /**
     * Scrapes and saves company's address from company detail page
     *
     * @return scraped address
     */
    private AddressData getCompanyAddress() {
        String city = getNullIfEmpty(document.select("[title=\"Municipality\"] p").text());
        String street = getNullIfEmpty(document.select("[title=\"street\"] p").text());
        String postalCode = getNullIfEmpty(document.select("[title=\"postal code\"] p").text());
        String countryOfficialName = getNullIfEmpty(document.select("[title=\"State\"] p").text());
        String landRegistryNumber = getNullIfEmpty(document.select("[title=\"Land registry number\"] p").text());
        String buildingNumber = getNullIfEmpty(
                document.select("[title=\"building number\"] p").text());
        return new AddressData(countryOfficialName, city, postalCode, street, buildingNumber, landRegistryNumber, null);
    }

    private BigDecimal getPriceVAT() {
        return getBigDecimalFromString(document.select("[title=\"Bid price incl. VAT\"] p").text());
    }

    private String getOrganisationId() {
        return getNullIfEmpty(document.select("[title=\"Organisation ID number\"] p").text());
    }

    private String getVATIdNumber() {
        return getNullIfEmpty(document.select("[title=\"VAT ID number\"] p").text());
    }

    private Boolean getIsAssociationOfSuppliers() {
        return convertYesNoToBoolean(
                document.select("div.gov-grid-tile:has(h3:contains(Selected supplier is an association of suppliers)) p").text());
    }

    private Boolean getIsRejectedDueTooLow() {
        return convertYesNoToBoolean(document.select("[title=\"The participant's bid has been rejected due to " +
                "the exceptionally low bid price\"] p").text());
    }

    private Boolean getIsWithdrawn() {
        return convertYesNoToBoolean(
                document.select("[title=\"The participant in the tender procedure has withdrawn from the tender" +
                        " procedure, refused to enter into a contract or failed to provide the contracting authority with" +
                        " the required cooperation\"] p").text());
    }
}