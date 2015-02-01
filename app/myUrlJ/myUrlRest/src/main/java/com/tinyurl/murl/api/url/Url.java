package com.tinyurl.murl.api.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyurl.murl.api.BaseService;
import com.tinyurl.murl.dto.ServiceResponseDTO;
import com.tinyurl.myurl.dto.url.UrlDTO;
import com.tinyurl.myurl.service.url.UrlService;

@Component
@Path("/api/url")
public class Url extends BaseService {
        
    private static final Logger LOGGER = Logger.getLogger(Url.class.getName());
    protected static final String ERROR_GET_URL = "CANNOT_READ_URL";
    protected static final String ERROR_CODES_JSON_PARSE_ERROR = "ERROR_CODES_JSON_PARSE_ERROR";
    protected static final String ERROR_CODES_IO_ERROR = "ERROR_CODES_IO_ERROR";
    
    @Autowired
    UrlService urlService;
    
    @POST
    @Path("/getUrl/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceResponseDTO getUsersByOrganization(InputStream inputStream) throws MalformedURLException {
        
        String json = getJSON(inputStream);
        ObjectMapper mapper = new ObjectMapper();
        ServiceResponseDTO result = new ServiceResponseDTO();
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            UrlDTO dto = mapper.readValue(json, UrlDTO.class);
            String error = urlService.validateUrl(dto);
            if (error == null) {
                result.setData(urlService.updateUrl(dto));
            } else {
                result.setError(true);
                result.setError_code(error);
            }
        } catch (JsonParseException e) {
            LOGGER.log(Level.WARNING, SERVICE_EXCEPTION, e);
            result.setError(true);
            result.setError_code(ERROR_CODES_JSON_PARSE_ERROR);
        } catch (JsonMappingException e) {
            LOGGER.log(Level.WARNING, SERVICE_EXCEPTION, e);
            result.setError(true);
            result.setError_code(ERROR_CODES_JSON_PARSE_ERROR);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, SERVICE_EXCEPTION, e);
            result.setError(true);
            result.setError_code(ERROR_CODES_IO_ERROR);
        }

        return result;
    }

}