package cz.cvut.fit.bap.parser.controller.scrapper;


import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;
import cz.cvut.fit.bap.parser.controller.data.ContractData;
import cz.cvut.fit.bap.parser.controller.data.OfferData;
import cz.cvut.fit.bap.parser.controller.data.ProcurementResultPageData;
import cz.cvut.fit.bap.parser.helpers.HtmlFileCreator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


class ProcurementResultScrapperTest {
    ContractData contractData = new ContractData(new BigDecimal("266400.00"), new BigDecimal("322344.00"),
            new BigDecimal("266400.00"), new BigDecimal("322344.00"),
            "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846408",
            "Nej.cz s.r.o.", Currency.CZK, LocalDate.of(2022, 5, 30));

    OfferData offer1 = new OfferData(new BigDecimal("266400.00"), new BigDecimal("322344.00"),
            "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846394",
            "Nej.cz s.r.o.", Currency.CZK);
    OfferData offer2 = new OfferData(new BigDecimal("378048.00"), new BigDecimal("457438.08"),
            "/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek/detail-uverejneni/1371846393",
            "O2 Czech Republic a.s.", Currency.CZK);

    private final ProcurementResultPageData expectedData = new ProcurementResultPageData(
            List.of(offer1, offer2),
            List.of(contractData)
    );

    @Test
    void getPageData() throws IOException {
        String url = "https://nen.nipez.cz/en/profily-zadavatelu-platne/detail-profilu/BSMV/uzavrene-zakazky/detail-zakazky/N006-22-V00010946/vysledek";
        Document document = Jsoup.parse(new HtmlFileCreator().ensureCreatedHtmlFile(url, "ProcurementResult.html"));
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);

        ProcurementResultPageData actualData = procurementResultScrapper.getPageData();
        Assertions.assertEquals(expectedData, actualData);
    }

    @Test
    void testMultipleSuppliers() {
        String html = """
                <div class="gov-content-block" title="Supplier with Whom the Contract Has Been Entered into">
                            <table class="gov-table gov-table--tablet-block gov-sortable-table">
                                <tbody class="gov-table__body">
                                <tr class="gov-table__row">
                                    <td class="gov-table__cell" data-title="Closing date of the contract" title="26/05/2023"
                                        style="width: 130px;">26. 05. 2023
                                    </td>
                                    <td class="gov-table__cell" data-title="Official name" title="supplier1" style="width: 100%;">
                                        supplier1
                                    </td>
                                    <td class="gov-table__cell" data-title="Contractual price excl. VAT" title="1,00"
                                        style="width: 100%;">1,00
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--last" data-title="Currency" title="CZK"
                                        style="width: 100%;">CZK
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                                        style="display: none; visibility: hidden;"><a class="gov-link gov-link--has-arrow"
                                                                                      aria-label="Show detail Netfox s.r.o."
                                                                                      href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                            class="gov-table__row-button-text">Detail</span></a></td>
                                </tr>
                                        <tr class="gov-table__row">
                                    <td class="gov-table__cell" data-title="Closing date of the contract" title="26/05/2023"
                                        style="width: 130px;">23. 05. 2020
                                    </td>
                                    <td class="gov-table__cell" data-title="Official name" title="supplier2" style="width: 100%;">
                                        supplier2
                                    </td>
                                    <td class="gov-table__cell" data-title="Contractual price excl. VAT" title="1,00"
                                        style="width: 100%;">1,00
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--last" data-title="Currency" title="CZK"
                                        style="width: 100%;">EUR
                                    </td>
                                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                                        style="display: none; visibility: hidden;"><a class="gov-link gov-link--has-arrow"
                                                                                      aria-label="Show detail Netfox s.r.o."
                                                                                      href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539262"><span
                                            class="gov-table__row-button-text">Detail</span></a></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    """;
        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        ContractData expectedOfferDto1 = new ContractData(new BigDecimal("1.00"), null, null, null,
                "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261",
                "supplier1", Currency.CZK, LocalDate.of(2023, 5, 26));
        ContractData expectedOfferDto2 = new ContractData(new BigDecimal("1.00"), null, null, null,
                "/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539262",
                "supplier2", Currency.EUR, LocalDate.of(2020, 5, 23));

        List<ContractData> actualOffers = procurementResultScrapper.getPageData().suppliers();
        Assertions.assertEquals(2, actualOffers.size());
        Assertions.assertEquals(expectedOfferDto1, actualOffers.get(0));
        Assertions.assertEquals(expectedOfferDto2, actualOffers.get(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    <td class="gov-table__cell" data-title="Official name" title="" style="width: 100%;">
                    </td>
                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                        style="display: none; visibility: hidden;">
                        <a class="gov-link gov-link--has-arrow" aria-label="Show detail Netfox s.r.o."
                            href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                class="gov-table__row-button-text">Detail</span></a>
                    </td>
                    """,
            """
                    <td class="gov-table__cell gov-table__cell--narrow gov-table__cell u-display-block u-hide--from-tablet gov-table__row-controls"
                        style="display: none; visibility: hidden;">
                        <a class="gov-link gov-link--has-arrow" aria-label="Show detail Netfox s.r.o."
                            href="/en/verejne-zakazky/detail-zakazky/N006-23-V00009801/vysledek/detail-uverejneni/1668539261"><span
                                class="gov-table__row-button-text">Detail</span></a>
                    </td>
                    """,
            """
                    <td class="gov-table__cell" data-title="Official name" title="Netfox s.r.o." style="width: 100%;">
                        Netfox s.r.o.
                    </td>
                    """
    })
    void testInvalidSupplier(String arg) {

        String html = """
                < div class= "gov-content-block"title= "Supplier with Whom the Contract Has Been Entered into" >
                <table class= "gov-table gov-table--tablet-block gov-sortable-table" >
                <tbody class= "gov-table__body" >""" + arg + "</tbody>\n </table>\n </div>";

        Document document = Jsoup.parse(html);
        ProcurementResultScrapper procurementResultScrapper = new ProcurementResultScrapper(document);
        Assertions.assertThrows(MissingHtmlElementException.class, procurementResultScrapper::getPageData);
    }
}