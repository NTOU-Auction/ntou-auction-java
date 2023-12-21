package ntou.auction.spring.product.request;

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
public class PostNonFixedPriceProductRequest {

    @NotNull (message="商品名稱不得為空")
    @Length(min = 1, max = 128 , message = "商品名稱至多32個中文字")
    private String productName;

    @NotNull
    @Min (value = 1,message = "價格須為正整數")
    private Long upsetPrice; //lowest requested price

    @NotNull
    @Min (value = 1,message = "每口叫價須為正整數")
    private Long bidIncrement;

    @NotNull (message="商品數量不得為空")
    private Long productAmount;

    @NotNull
    private String finishTime;

    @Length(min = 1, max = 32)
    private String productType;

    @Length(min = 1, max = 20971520,message = "商品敘述過長")
    private String productDescription;

    @Lob
    @Column(length = 5242880)
    @Length(min = 1, max = 5242880 ,message = "圖片檔案過大，請重新上傳")
    private String productImage;



}
