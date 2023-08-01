package org.example.Pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TerminalDetailsPojo {

    private Long outletNumber;

    private Long terminalNumber;

    private String location;

    private String customerName;

}
