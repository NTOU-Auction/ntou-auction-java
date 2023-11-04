package ntou.auction.spring.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
    @Length(min = 1, max = 32)
    private String productName;

    @NotNull
    private Long price;

    @NotNull
    private Boolean isFixedPrice;

    @NotNull
    @Length(min = 1, max = 256)
    private String productDescription;

    @NotNull
    @Length(min = 1, max = 256)
    private String productScale;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    private byte[] productImage;

}
