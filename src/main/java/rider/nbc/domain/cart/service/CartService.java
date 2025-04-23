package rider.nbc.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.exception.CartExceptionCode;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.repository.MenuRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final MenuRepository menuRepository;

    public CartListResponseDto getCartList() {

        return null;
    }

    public CartItemResponseDto addCartItem(Long authId, CartAddRequestDto cartAddRequestDto) {
        //메뉴 아이디 값에 맞는 메뉴 받아오기 << 이거 옵션안할거면 menuRepository 에 bool 반환하는걸로
        // 함수있으면 될덧
        Menu menu = menuRepository.findById(cartAddRequestDto.getMenuId())
                .orElseThrow(() -> new CartException(CartExceptionCode.INVALID_CART_ITEM));

        //메뉴에서 (옵션-보류) 아이디 값 쓸거니깐 꺼내서 vo 만들어주기
        MenuItem menuItem = new MenuItem(menu.getId(), cartAddRequestDto.getQuantity());

        //만들어준거 저장 (유저 아이디 값, vo) 파라미터
        cartRedisRepository.addCartItem(authId,
                menu.getStore().getId(), menuItem);

        return CartItemResponseDto.builder()
                .storeId(menu.getStore().getId())
                .menuId(menu.getId())
                .price((int) (cartAddRequestDto.getQuantity() * menu.getPrice()))
                .quantity(cartAddRequestDto.getQuantity())
                .build();
    }
}
