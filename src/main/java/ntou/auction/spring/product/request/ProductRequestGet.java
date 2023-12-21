package ntou.auction.spring.product.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestGet {



    @Length(min = 1, max = 128)
    private String productName;

    @Length(min = 1, max = 32)
    private String productType;

    @NotNull(message = "請填寫搜尋方式")
    private String searchType;
}
