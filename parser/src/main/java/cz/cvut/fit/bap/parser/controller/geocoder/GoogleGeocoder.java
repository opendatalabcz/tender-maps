package cz.cvut.fit.bap.parser.controller.geocoder;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import cz.cvut.fit.bap.parser.controller.dto.AddressDto;
import cz.cvut.fit.bap.parser.controller.dto.converter.AddressDtoToAddress;
import cz.cvut.fit.bap.parser.domain.Address;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class for handling communication with Google's geocoding api.
 */
@Component
public class GoogleGeocoder implements Geocoder, AutoCloseable{
    private GeoApiContext context;
    private final AddressDtoToAddress addressDtoToAddress;
    private final String apiKey;

    public GoogleGeocoder(@Value("${GOOGLE_API_KEY:}") String apiKey,
                          AddressDtoToAddress addressDtoToAddress){
        if(!apiKey.isEmpty()){
            this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
        }
        this.apiKey = apiKey;
        this.addressDtoToAddress = addressDtoToAddress;
    }

    /**
     * Geocodes addressDto
     *
     * @param addressDto addressDto which is supposed to be geocoded
     * @return address with latitude, longitude and country code
     */
    @Override
    @Timed(value = "scrapper.google.geocode")
    public Address geocode(AddressDto addressDto){
        if(apiKey.isEmpty()){
            return addressDtoToAddress.apply(addressDto);
        }
        Address address = addressDtoToAddress.apply(addressDto);
        GeocodingResult[] geocodingResults = sendRequest(addressDto);
        if(geocodingResults == null || geocodingResults.length == 0){
            Metrics.counter("scrapper.google.geocode.failed").increment();
            return address;
        }
        if(address.getCountryCode() == null){
            address.setCountryCode(getCountryCode(geocodingResults));
        }
        address.setLongitude(getLongitude(geocodingResults));
        address.setLatitude(getLatitude(geocodingResults));
        return address;
    }

    private GeocodingResult[] sendRequest(AddressDto addressDto){
        String addressStr = addressDto.buildingNumber() + ' ' + addressDto.street() + ' ' +
                addressDto.city() + ' ' + addressDto.postalCode() + ' ' +
                addressDto.country();
        try{
            return GeocodingApi.geocode(context, addressStr).await();
        }catch(ApiException | IOException e){
            throw new GeocodingException(e);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new GeocodingException(e);
        }
    }

    /**
     * Gets country code from GeocodingResult
     *
     * @param results array of geocodingResult
     * @return two letters country code on success, null otherwise
     */
    private String getCountryCode(GeocodingResult[] results){
        AddressComponent[] addressComponents = results[0].addressComponents;
        for(AddressComponent addressComponent : addressComponents){
            for(int j = 0; j < addressComponent.types.length; j++){
                if(addressComponent.types[j].toString().equals("country")){
                    return addressComponent.shortName;
                }
            }
        }
        return null;
    }

    /**
     * Gets latitude from geocodingResult
     *
     * @param results array of geocodingResult
     * @return latitude
     */
    private double getLatitude(GeocodingResult[] results){
        return results[0].geometry.location.lat;
    }

    /**
     * Gets longitude from geocodingResult
     *
     * @param results array of geocodingResult
     * @return longitude
     */
    private double getLongitude(GeocodingResult[] results){
        return results[0].geometry.location.lng;
    }

    /**
     * Shuts down geocoding api context after all request were sent
     */
    @Override
    public void close(){
        context.shutdown();
    }
}