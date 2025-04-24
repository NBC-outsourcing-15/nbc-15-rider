package rider.nbc.domain.cart.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import rider.nbc.domain.cart.dto.request.CartAddRequestDto;
import rider.nbc.domain.cart.dto.request.CartUpdateRequestDto;
import rider.nbc.domain.cart.dto.response.CartItemResponseDto;
import rider.nbc.domain.cart.dto.response.CartListResponseDto;
import rider.nbc.domain.cart.vo.Cart;
import rider.nbc.domain.cart.exception.CartException;
import rider.nbc.domain.cart.repository.CartRedisRepository;
import rider.nbc.domain.cart.vo.MenuItem;
import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;
import rider.nbc.domain.menu.repository.MenuRepository;
import rider.nbc.domain.store.entity.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CartRedisRepository cartRedisRepository;

    @InjectMocks
    private CartService cartService;


    @Test
    @DisplayName("addCartItem 성공 - 장바구니에 메뉴 추가 성공")
    void addCartItem_success(){
        //given
        Store store = new Store();
        ReflectionTestUtils.setField(store, "id", 1L);

        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "store", store);
        ReflectionTestUtils.setField(menu, "price", 8000L);

        CartAddRequestDto requestDto = new CartAddRequestDto(menu.getId(), 2);
        Long authId = 1L;
        MenuItem menuItem = new MenuItem(menu.getId(), menu.getPrice(), "밥", requestDto.getQuantity());

        Cart cart = new Cart(1L, store.getId(), menuItem);


        given(menuRepository.findByIdOrElseThrow(requestDto.getMenuId())).willReturn(menu);
        given(cartRedisRepository.findById(authId)).willReturn(Optional.empty());
        given(cartRedisRepository.save(any(Cart.class))).willReturn(cart);
        //when
        CartItemResponseDto result = cartService.addCartItem(authId, requestDto);

        //then
        assertNotNull(result);
        assertEquals(store.getId(), result.getStoreId());
        assertEquals(menu.getId(), result.getMenuId());
        assertEquals(2, result.getQuantity());
        assertEquals(requestDto.getQuantity() * menu.getPrice(), result.getPrice()); // 1000 * 2
    }

    @Test
    @DisplayName("addCartItem 실패 - 유효하지 않는 메뉴")
    void addCartItem_INVALID_CART_ITEM(){
        // given
        Long authId = 1L;
        Long invalidMenuId = 999L;
        CartAddRequestDto requestDto = new CartAddRequestDto(invalidMenuId, 1);


        given(menuRepository.findByIdOrElseThrow(invalidMenuId))
                .willThrow(new MenuException(MenuExceptionCode.MENU_NOT_FOUND));

        // when, then
        MenuException exception = assertThrows(MenuException.class, () ->
                cartService.addCartItem(authId, requestDto));
        assertEquals("메뉴를 찾을 수 없습니다.", exception.getMessage());
    }

    @Nested
    @DisplayName("updateCart ")
    class updateCart_Test{
        Long userId = 1L;

        Long store1Id = 101L;
        Long store2Id = 102L;

        Store store1 = new Store();

        Menu menu1 = new Menu();
        Menu menu2 = new Menu();

        List<MenuItem> menuItems = List.of(
                new MenuItem(1L, null, null, 2),
                new MenuItem(2L, null, null, 1)
        );

        @Test
        @DisplayName("updateCart 성공 - 모두 같은 가게의 메뉴들 수정")
        void updateCart_successs(){
            //given
            CartUpdateRequestDto requestDto = new CartUpdateRequestDto(store1Id, menuItems);

            ReflectionTestUtils.setField(menu1, "id", 1L);
            ReflectionTestUtils.setField(menu2, "id", 2L);
            ReflectionTestUtils.setField(menu1, "name", "Burger");
            ReflectionTestUtils.setField(menu2, "name", "Fries");
            ReflectionTestUtils.setField(menu1, "price", 8000L);
            ReflectionTestUtils.setField(menu2, "price", 2000L);
            ReflectionTestUtils.setField(store1, "id", store1Id);
            ReflectionTestUtils.setField(menu1, "store", store1);
            ReflectionTestUtils.setField(menu2, "store", store1);

            Cart userCart = new Cart();
            ReflectionTestUtils.setField(userCart, "userId", userId);
            ReflectionTestUtils.setField(userCart, "storeId", store1Id);
            ReflectionTestUtils.setField(userCart, "menus", new ArrayList<>());

            Cart updatedCart = new Cart();
            menuItems.get(0).setInfos(8000L, "Burger");
            menuItems.get(1).setInfos(2000L, "Fries");
            ReflectionTestUtils.setField(updatedCart, "userId", userId);
            ReflectionTestUtils.setField(updatedCart, "storeId", store1Id);
            ReflectionTestUtils.setField(updatedCart, "menus", menuItems);

            given(cartRedisRepository.findById(userId)).willReturn(Optional.of(userCart));
            given(menuRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(menu1, menu2));
            given(cartRedisRepository.save(any(Cart.class))).willReturn(updatedCart);

            // when
            CartListResponseDto response = cartService.updateCart(userId, requestDto);

            // then
            assertEquals(store1Id, response.getStoreId());
            assertEquals(2, response.getCartMenus().size());

            MenuItem item1 = response.getCartMenus().get(0);
            assertEquals(1L, item1.getMenuId());
            assertEquals("Burger", item1.getName());
            assertEquals(8000L, item1.getPrice());
            assertEquals(2, item1.getQuantity());

            MenuItem item2 = response.getCartMenus().get(1);
            assertEquals(2L, item2.getMenuId());
            assertEquals("Fries", item2.getName());
            assertEquals(2000L, item2.getPrice());
            assertEquals(1, item2.getQuantity());
        }

        @Test
        @DisplayName("updateCart 실패 - 서로다른가게의메뉴")
        void updateCart_INVALID_STORE_ID() {
            // given
            Store store2 = new Store();

            CartUpdateRequestDto requestDto = new CartUpdateRequestDto(store1Id, menuItems);

            ReflectionTestUtils.setField(menu1, "id", 1L);
            ReflectionTestUtils.setField(menu2, "id", 2L);
            ReflectionTestUtils.setField(menu1, "name", "Burger");
            ReflectionTestUtils.setField(menu2, "name", "Fries");
            ReflectionTestUtils.setField(menu1, "price", 8000L);
            ReflectionTestUtils.setField(menu2, "price", 2000L);
            ReflectionTestUtils.setField(store1, "id", store1Id);
            ReflectionTestUtils.setField(store2, "id", store2Id);
            ReflectionTestUtils.setField(menu1, "store", store1);
            ReflectionTestUtils.setField(menu2, "store", store2);

            menuItems.get(0).setInfos(8000L, "Burger");
            Cart userCart = new Cart(userId, store1Id, menuItems.get(0));

            given(cartRedisRepository.findById(userId)).willReturn(Optional.of(userCart));
            given(menuRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(menu1, menu2));

            // when, then
            CartException exception = assertThrows(CartException.class, () ->
                    cartService.updateCart(userId, requestDto));
            assertEquals("같은 가게만 장바구니 목록에 담을 수 있습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteSelectedMenu ")
    class deleteSelectedMenu_Test{
        Long userId = 1L;

        Long store1Id = 101L;
        Menu menu1 = new Menu();
        Menu menu2 = new Menu();

        List<MenuItem> menuItems = new ArrayList<>(List.of(
                new MenuItem(1L, null, null, 2),
                new MenuItem(2L, null, null, 1)
        ));

        @Test
        @DisplayName("deleteSelectedMenu 성공 - 선택한 메뉴 삭제")
        void deleteSelectedMenu_success(){
            // given
            ReflectionTestUtils.setField(menu1, "id", 1L);
            ReflectionTestUtils.setField(menu2, "id", 2L);
            ReflectionTestUtils.setField(menu1, "name", "Burger");
            ReflectionTestUtils.setField(menu2, "name", "Fries");
            ReflectionTestUtils.setField(menu1, "price", 8000L);
            ReflectionTestUtils.setField(menu2, "price", 2000L);

            menuItems.get(0).setInfos(8000L, "Burger");
            menuItems.get(1).setInfos(2000L, "Fries");

            Cart cart = new Cart();
            ReflectionTestUtils.setField(cart, "userId", userId);
            ReflectionTestUtils.setField(cart, "storeId", store1Id);
            ReflectionTestUtils.setField(cart, "menus", menuItems);

            Cart deletedCart = new Cart(userId, store1Id, menuItems.get(1));

            given(cartRedisRepository.findById(userId)).willReturn(Optional.of(cart));
            given(cartRedisRepository.save(any(Cart.class))).willReturn(deletedCart);

            //when
            CartListResponseDto result = cartService.deleteSelectedMenu(userId, 1L);

            //then
            assertEquals(store1Id, result.getStoreId());
            assertEquals(1, result.getCartMenus().size());

            MenuItem remainingItem = result.getCartMenus().get(0);
            assertEquals(2L, remainingItem.getMenuId());
            assertEquals("Fries", remainingItem.getName());
            assertEquals(2000L, remainingItem.getPrice());
            assertEquals(1, remainingItem.getQuantity());
        }

        @Test
        @DisplayName("deleteSelectedMenu 실패 - 장바구니 없음")
        void deleteSelectedMenu_NO_CONTENTS(){
            // give
            given(cartRedisRepository.findById(userId)).willReturn(Optional.empty());

            // when, then
            CartException exception = assertThrows(CartException.class, () ->
                    cartService.deleteSelectedMenu(userId, 1L));
            assertEquals("장바구니가 비어있습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteCart  ")
    class deleteCart_Test{
        Long userId = 1L;

        @Test
        @DisplayName("deleteCart 성공 - 장바구니 초기화")
        void deleteCart_success(){
            cartService.deleteCart(userId);

            verify(cartRedisRepository,times(1)).deleteById(anyLong());
        }
    }
}