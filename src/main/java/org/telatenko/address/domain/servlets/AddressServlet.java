package org.telatenko.address.domain.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telatenko.address.domain.daos.AddressDao;
import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.dtos.AddressDto;
import org.telatenko.address.domain.mappers.AddressMapper;
import org.telatenko.address.domain.models.Address;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/addresses/*")
public class AddressServlet extends HttpServlet {

    private AddressDao addressDao = new AddressDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnector.runLiquibase();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Address> addresses = addressDao.getAllAddresses();
            List<AddressDto> addressDtos = AddressMapper.INSTANCE.toDtos(addresses);
            String json = objectMapper.writeValueAsString(addressDtos);
            response.setContentType("application/json");
            response.getWriter().write(json);
        } else {
            String addressId = pathInfo.substring(1);
            Address address = addressDao.getAddressById(Integer.parseInt(addressId));
            if (address != null) {
                AddressDto addressDto = AddressMapper.INSTANCE.toDto(address);
                String json = objectMapper.writeValueAsString(addressDto);
                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Address newAddress = objectMapper.readValue(request.getReader(), Address.class);
        addressDao.addAddress(newAddress);
        AddressDto addressDto = AddressMapper.INSTANCE.toDto(newAddress);
        String json = objectMapper.writeValueAsString(addressDto);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(json);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String addressId = pathInfo.substring(1);
            Address updatedAddress = objectMapper.readValue(request.getReader(), Address.class);
            updatedAddress.setId(Integer.parseInt(addressId));
            Address updateAddressFromDb = addressDao.updateAddress(updatedAddress);
            if (updatedAddress != null) {
                AddressDto addressDto = AddressMapper.INSTANCE.toDto(updateAddressFromDb);
                String json = objectMapper.writeValueAsString(addressDto);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), updatedAddress);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Address not found");
            }
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String addressId = pathInfo.substring(1);
            addressDao.deleteAddress(Integer.parseInt(addressId));
        }
    }
}
