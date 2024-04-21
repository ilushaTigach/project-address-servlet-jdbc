package org.telatenko.address.domain.servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.telatenko.address.domain.daos.PhoneDao;
import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.dtos.PhoneDto;
import org.telatenko.address.domain.mappers.PhoneMapper;
import org.telatenko.address.domain.models.Phone;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/phones/*")
public class PhoneServlet extends HttpServlet {

    private PhoneDao phoneDao = new PhoneDao();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnector.runLiquibase();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Phone> phones = phoneDao.getAllPhones();
            List<PhoneDto> phoneDtos = PhoneMapper.INSTANCE.toDtos(phones);
            String json = objectMapper.writeValueAsString(phoneDtos);
            response.setContentType("application/json");
            response.getWriter().write(json);
        } else {
            String phoneId = pathInfo.substring(1);
            Phone phone = phoneDao.getPhoneById(Integer.parseInt(phoneId));
            if (phone != null) {
                PhoneDto phoneDto = PhoneMapper.INSTANCE.toDto(phone);
                String json = objectMapper.writeValueAsString(phoneDto);
                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Phone newPhone = objectMapper.readValue(request.getReader(), Phone.class);
        phoneDao.addPhone(newPhone);
        PhoneDto phoneDto = PhoneMapper.INSTANCE.toDto(newPhone);
        String json = objectMapper.writeValueAsString(phoneDto);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(json);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String phoneId = pathInfo.substring(1);
            Phone updatedPhone = objectMapper.readValue(request.getReader(), Phone.class);
            updatedPhone.setId(Integer.parseInt(phoneId));

            Phone updatedPhoneFromDb = phoneDao.updatePhone(updatedPhone);
            if (updatedPhoneFromDb != null) {
                PhoneDto phoneDto = PhoneMapper.INSTANCE.toDto(updatedPhoneFromDb);
                String json = objectMapper.writeValueAsString(phoneDto);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to update phone");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String phoneId = pathInfo.substring(1);
            phoneDao.deletePhone(Integer.parseInt(phoneId));
        }
    }
}
