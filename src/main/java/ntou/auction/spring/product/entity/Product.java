package ntou.auction.spring.product.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.util.AbstractEntity;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends AbstractEntity {

    @NotNull
    @Length(min = 1, max = 128)
    private String productName;


    @Length(min = 1, max = 32)
    private String productType;


    @NotNull
    private Boolean isFixedPrice;


    @Length(min = 1, max = 20971520)
    private String productDescription;

    @NotNull
    private Long sellerID;

    private String sellerName;

    private Long productAmount;

    //followings are non-isFixedPrice feature

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "bidInfo")
    private Map<Long,Long> bidInfo;

    private Long upsetPrice; //lowest requested price

    @NotNull
    private Long currentPrice;

    private Long bidIncrement;

    private Boolean isAuction; //競標商品已經被加進購物車?

    private Boolean visible;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    @Length(min = 1, max = 5242880)
    private String productImage;

    public boolean isExpired() {
        if(isFixedPrice){
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(this.finishTime);
    }
}
