package data.dto;

import data.model.Native;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NativeDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;


    public static Native unpack(NativeDto nativeDto){
        return  new Native(nativeDto.getFirstName(), nativeDto.getLastName(), nativeDto.getEmail(), nativeDto.getPassword());
    }

}
