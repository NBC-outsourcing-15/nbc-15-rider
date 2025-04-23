package rider.nbc.domain.cart.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.exception.CartExceptionCode;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRedisRepositoryImpl implements CartRedisRepository{

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void addCartItem(Long userId, Long storeId, MenuItem value) {
        String key = "cart:" + userId;

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //기존 장바구니 확인
        Cart cart = getCartData(userId);
        if(cart == null || !cart.getStoreId().equals(storeId) ){ //장바구니 내용 초기화하는 경우
            //가게 아이디 다르면 덮어쓰기
            cart = new Cart(userId, storeId, value);
        }else {
            //아니면 뒤로 붙이기
            Optional<MenuItem> existingItemOpt = cart.getMenus().stream()
                    .filter(item -> item.getMenuId().equals(value.getMenuId()))
                    .findFirst();

            // 붙이기전에 메뉴 중복아이디 있으면 quantity 값만 조절
            if (existingItemOpt.isPresent()) {
                existingItemOpt.get().updateQuantity(value.getQuantity());
            } else {
                // 없으면 menus 에 추가
                cart.getMenus().add(value);
            }
        }
        // 다시 Redis에 저장
        String updatedCart;
        try {
            updatedCart = objectMapper.writeValueAsString(cart);
        } catch (JsonProcessingException e) {
            throw new CartException(CartExceptionCode.CART_SAVE_FAILED);
        }
        ops.set(key, updatedCart, Duration.ofHours(24));
        //redisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    public Cart getCartData(Long userId) {
        String key = "cart:" + userId;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, Cart.class);
        } catch (JsonProcessingException e) {
            throw new CartException(CartExceptionCode.CART_READ_FAILED);
        }
    }

}
