package ntou.auction.spring.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    private Long price;

    @NotNull
    private Boolean isFixedPrice;


    @Length(min = 1, max = 256)
    private String productDescription;

    @NotNull
    private Long sellerID;

    //followings are non-isFixedPrice feature


    private Long upsetPrice; //lowest requested price


    private Long currentPrice;



    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    @Length(min = 1, max = 5242880)
    private String productImage;

}
