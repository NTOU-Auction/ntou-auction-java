package ntou.auction.spring.data.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest{


    @Length(min = 1, max = 32)
    private String productName;


    @Length(min = 1, max = 32)
    private String productType;


    private String searchType;



}
