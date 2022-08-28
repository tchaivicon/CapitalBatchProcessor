package com.hyundai.capital.capitalbatchprocessor.entity.tutorials.sales;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class SalesSum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private long amountSum;

    @Builder
    public SalesSum(LocalDate orderDate, long amountSum) {
        this.orderDate = orderDate;
        this.amountSum = amountSum;
    }
}