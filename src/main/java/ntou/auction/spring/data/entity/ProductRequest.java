package ntou.auction.spring.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest{

    @NotNull
    @Length(min = 1, max = 32 , message = "商品名稱至多32個中文字") //32個中文字
    private String productName;
/*
    @NotNull
    private Long price;
*/
    @Length(min = 1, max = 32)
    private String productType;
/*
    @NotNull
    private Boolean isFixedPrice;

    @Length(min = 1, max = 256)
    private String productDescription;

    private Long upsetPrice; //lowest requested price

    private Long currentPrice;

    @Lob
    @Column(length = 5242880)
    private String productImage;
*/
    private String searchType;
}
