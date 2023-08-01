package org.example.Pojos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatementPojo {

    String customerName;

    Long outletNumber;

    Long terminalNumber;
    String transactionAmount;
    String cardNumber;

    String transactionDate;




}
