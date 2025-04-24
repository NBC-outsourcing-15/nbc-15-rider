package rider.nbc.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.request.CartUpdateRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.entity.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.exception.CartExceptionCode;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.repository.MenuRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final MenuRepository menuRepository;

    /**
     * [Service] 장바구니 조회
     *
     * @param authId 로그인한 유저 id
     * @return 유저의 장바구니
     * -> 장바구니 내역없으면 NO contents
     */
    public CartListResponseDto getCartList(Long authId) {
        Cart cart = cartRedisRepository.findById(authId)
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_IS_EMPTY));

        return new CartListResponseDto(cart);
    }

    @Transactional
    public CartItemResponseDto addCartItem(Long authId, CartAddRequestDto cartAddRequestDto) {
        Menu menu = menuRepository.findByIdOrElseThrow(cartAddRequestDto.getMenuId());

        //메뉴에서 (옵션-보류) 아이디 값 쓸거니깐 꺼내서 vo 만들어주기
        MenuItem menuItem = MenuItem.builder()
                .menuId(menu.getId())
                .price(menu.getPrice())
                .name(menu.getName())
                .quantity(cartAddRequestDto.getQuantity())
                .build();

        Cart cart = cartRedisRepository.findById(authId)
                .orElse(null);

        if (cart == null || !cart.getStoreId().equals(menu.getStore().getId())) { // 장바구니 생성
            cart = new Cart(authId, menu.getStore().getId(), menuItem);
        } else {
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

    @Transactional
    public CartListResponseDto updateCart(Long authId, CartUpdateRequestDto requestDto) {
        Cart cart = cartRedisRepository.findById(authId)
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_IS_EMPTY));

        //모든 menuId 꺼내기
        List<Long> menuIds = requestDto.getCartMenus().stream()
                .map(MenuItem::getMenuId)
                .toList();

        //해당 메뉴id들이 모두 같은 가게여야함.
        List<Menu> menus = menuRepository.findAllById(menuIds);
        boolean allMatch = menus.stream()
                .allMatch(menu -> menu.getStore().getId().equals(requestDto.getStoreId()));
        // 다른것이 하나라도 있으면 오류 반환
        if (!allMatch) {
            throw new CartException(CartExceptionCode.INVALID_STORE_ID);
        }
        // menuId -> Menu 매핑 (성능을 위해 Map 사용)
        Map<Long, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));

        // MenuItem 에 가격과 이름 세팅
        List<MenuItem> enrichedItems = requestDto.getCartMenus().stream()
                .map(item -> {
                    Menu menu = menuMap.get(item.getMenuId());
                    MenuItem newItem = MenuItem.builder()
                            .menuId(item.getMenuId())
                            .quantity(item.getQuantity())
                            .build();
                    newItem.setInfos(menu.getPrice(), menu.getName());
                    return newItem;
                })
                .toList();

        cart.updateCart(menus.get(0).getStore().getId(), enrichedItems);
        //저장
        Cart updatedCart = cartRedisRepository.save(cart);
        return new CartListResponseDto(updatedCart);
    }

    /**
     * [Service] 단일 메뉴 제거
     *
     * @param authId 로그인 ID
     * @param itemId 지울 아이템 ID
     * @return 수정 후 장바구니 내용
     */
    public CartListResponseDto deleteSelectedMenu(Long authId, Long itemId) {
        Cart cart = cartRedisRepository.findById(authId)
                .orElseThrow(() -> new CartException(CartExceptionCode.CART_IS_EMPTY));

        cart.removeMenuItem(itemId);
        cartRedisRepository.save(cart);
        return new CartListResponseDto(cart);
    }

    /**
     * [Service] 장바구니 제거
     * @param authId 로그인 ID
     */
    public void deleteCart(Long authId){
        cartRedisRepository.deleteById(authId);
    }
}
