package org.telatenko.address.domain.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telatenko.address.domain.daos.UserDao;
import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.dtos.UserDto;
import org.telatenko.address.domain.mappers.UserMapper;
import org.telatenko.address.domain.models.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private UserDao userDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserServlet() {
        try {
            this.userDao = new UserDao(DatabaseConnector.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserServlet(Connection connection) {
        this.userDao = new UserDao(connection);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseConnector.runLiquibase();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<User> users = userDao.getAllUsers();
            List<UserDto> userDtos = UserMapper.INSTANCE.toDto(users);
            String json = objectMapper.writeValueAsString(userDtos);
            response.setContentType("application/json");
            response.getWriter().write(json);
        } else {
            String userId = pathInfo.substring(1);
            User user = userDao.getUserById(Integer.parseInt(userId));
            if (user != null) {
                UserDto userDto = UserMapper.INSTANCE.toDto(user);
                String json = objectMapper.writeValueAsString(userDto);
                response.setContentType("application/json");
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDto newUser = objectMapper.readValue(request.getReader(), UserDto.class);
        User user = UserMapper.INSTANCE.toEntity(newUser);
        UserDto userDto = UserMapper.INSTANCE.toDto(userDao.addUser(user));
        String json = objectMapper.writeValueAsString(userDto);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(json);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String userId = pathInfo.substring(1);
            User updatedUser = objectMapper.readValue(request.getReader(), User.class);
            updatedUser.setId(Integer.parseInt(userId));
            User updatedUserFromDb = userDao.updateUser(updatedUser);
            if (updatedUserFromDb != null) {
                UserDto userDto = UserMapper.INSTANCE.toDto(updatedUserFromDb);
                String json = objectMapper.writeValueAsString(userDto);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(json);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to update user");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String userId = pathInfo.substring(1);
            userDao.deleteUser(Integer.parseInt(userId));
        }
    }
}