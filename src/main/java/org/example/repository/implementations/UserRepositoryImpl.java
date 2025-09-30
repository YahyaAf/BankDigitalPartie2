package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.User;
import org.example.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class UserRepositoryImpl implements UserRepository {

    private final Connection connection;

    public UserRepositoryImpl(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void save(User user){
        String sql = "INSERT INTO users (id, name, email, password, role ) VALUES (?,?,?,?,?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,user.getId());
            stmt.setString(2,user.getName());
            stmt.setString(3,user.getEmail());
            stmt.setString(4,user.getPassword());
            stmt.setObject(5,user.getRole().name(), java.sql.Types.OTHER);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public Optional<User> findById(UUID id){
        String sql = "SELECT * FROM users WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToUser(rs));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email){
        String sql = "SELECT * FROM users WHERE email = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToUser(rs));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<User> findAll(){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                users.add(mapToUser(rs));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return users;
    }

    public void update(User user){
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setObject(4, user.getId());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteById(UUID id){
        String sql = "DELETE FROM users WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private User mapToUser(ResultSet rs) throws SQLException {
        return new User(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                User.Role.valueOf(rs.getString("role")) // convert string -> enum
        );
    }


}
