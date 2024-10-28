package com.huuduc.authservice.mapper;

import com.huuduc.authservice.dto.UserResponse;
import com.huuduc.authservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

   public UserResponse fromUser(User user){

       return new UserResponse(
               user.getId(),
               user.getUsername()
       );
   }

}
