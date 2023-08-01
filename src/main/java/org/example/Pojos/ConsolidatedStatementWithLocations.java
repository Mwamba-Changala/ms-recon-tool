package org.example.Pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsolidatedStatementWithLocations {


    String customerName;

    Long outletNumber;

    Long terminalNumber;

    String cardNumber;

    String transactionAmount;

    String location;


//    List<TerminalDetailsPojo> terminalDetailsPojoList;
}
