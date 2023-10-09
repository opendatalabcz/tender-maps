package cz.cvut.fit.bap.parser.controller.dto;

import cz.cvut.fit.bap.parser.controller.currency_exchanger.Currency;

import java.math.BigDecimal;

/*
    Row from participants table on procurement result page
 */
public record OfferDto(
        BigDecimal price,
        String detailHref,
        String companyName,
        Currency currency
){
}