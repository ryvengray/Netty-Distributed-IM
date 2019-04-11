package xin.ryven.project.common.entity;

import lombok.*;

/**
 * @author gray
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class User {

    private Integer userId;

    private String username;

    private String password;

    public User(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

}
