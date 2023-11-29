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
public class BidRequest {

    @NotNull (message="商品ID不得為空")
    private Long productID;

    @NotNull (message="出價不得為空")
    @Min (value = 1,message = "出價須為正整數")
    private Long bid;

}
