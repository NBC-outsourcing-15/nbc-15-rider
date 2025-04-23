package rider.nbc.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.repository.MenuRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final MenuRepository menuRepository;

    public CartListResponseDto getCartList() {

        return null;
    }

    public CartItemResponseDto addCartItem(Long authId, CartAddRequestDto cartAddRequestDto) {
        Menu menu = menuRepository.findByIdOrElseThrow(cartAddRequestDto.getMenuId());

        //메뉴에서 (옵션-보류) 아이디 값 쓸거니깐 꺼내서 vo 만들어주기
        MenuItem menuItem = new MenuItem(menu.getId(), cartAddRequestDto.getQuantity());

        Cart cart = cartRedisRepository.findById(authId)
                .orElse(null);

        if(cart == null || !cart.getStoreId().equals(menu.getStore().getId())){ // 장바구니 생성
            cart = new Cart(authId, menu.getStore().getId(), menuItem);
        }else{
            Optional<MenuItem> existingItemOpt = cart.getMenus().stream()
                    .filter(item -> item.getMenuId().equals(menuItem.getMenuId()))
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                existingItemOpt.get().updateQuantity(menuItem.getQuantity());
            } else {
                cart.getMenus().add(menuItem);
            }
        }

        //만들어준거 저장 (유저 아이디 값, vo) 파라미터
        Cart savedCart = cartRedisRepository.save(cart);

        return CartItemResponseDto.builder()
                .storeId(savedCart.getStoreId())
                .menuId(menu.getId())
                .price((int) (menuItem.getQuantity() * menu.getPrice()))
                .quantity(menuItem.getQuantity())
                .build();
    }
}
