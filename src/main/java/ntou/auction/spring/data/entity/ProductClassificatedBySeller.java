package ntou.auction.spring.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductClassificatedBySeller {
    private Map<String, List<ProductAddAmount>> ProductShowBySeller = new HashMap<>();
}
