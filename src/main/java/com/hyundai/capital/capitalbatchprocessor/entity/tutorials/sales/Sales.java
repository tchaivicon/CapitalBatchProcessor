package com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private Long amount;
    private String orderNo;

    @Builder
    public Sales(LocalDate orderDate, Long amount, String orderNo){
        this.orderDate = orderDate;
        this.amount = amount;
        this.orderNo = orderNo;
    }
}
