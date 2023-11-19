package ntou.auction.spring.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest{

    @NotNull (message="商品名稱不得為空")
    @Length(min = 1, max = 128 , message = "商品名稱至多32個中文字")
    private String productName;

    @NotNull (message="價格不得為空")
    @Min (value = 0,message = "價格不得為零")
    private Long price;

    @NotNull (message = "請填寫販售方式")
    private Boolean isFixedPrice;

    @Length(min = 1, max = 32)
    private String productType;

    @Length(min = 1, max = 256,message = "商品敘述過長")
    private String productDescription;

    @Lob
    @Column(length = 5242880)
    @Length(min = 1, max = 5242880 ,message = "圖片檔案過大，請重新上傳")
    private String productImage;


    private String searchType;
}
