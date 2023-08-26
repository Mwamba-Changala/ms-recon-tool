package org.example.Pojos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ResponsePojo {

    private Integer statusCode;

    private Object message;

}
