package rider.nbc.domain.cart.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import rider.nbc.domain.cart.entity.Cart;

import rider.nbc.domain.cart.vo.MenuItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class CartRedisRepositoryImplTest {

    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    //@InjectMocks
    private CartRedisRepositoryImpl cartRedisRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cartRedisRepository = new CartRedisRepositoryImpl(objectMapper, redisTemplate);
    }

    @Test
    @DisplayName("addCartItem 성공 - 새로운 장바구니 생성")
    void addCartItem_CartNull(){
        //given
        Long userId = 1L;
        Long storeId = 100L;
        MenuItem menuItem = new MenuItem(1L, 2);
        // 해당 유저 장바구니 없음
        given(valueOperations.get("cart:" + userId)).willReturn(null);

        //when
        cartRedisRepository.addCartItem(userId, storeId, menuItem);

        //then
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(valueOperations, atLeastOnce()).set(keyCaptor.capture(), valueCaptor.capture(), any());

        String savedKey = keyCaptor.getValue();
        String savedValue = valueCaptor.getValue();

        assertEquals("cart:" + userId, savedKey);
        assertTrue(savedValue.contains("\"storeId\":100"));
        assertTrue(savedValue.contains("\"menuId\":1"));
    }

    @Test
    @DisplayName("addCartItem 성공 - 가게 ID 다름 > 장바구니 덮어쓰기")
    void addCartItem_CartExists_DifferentStore() throws JsonProcessingException {
        // given
        Long userId = 1L;
        Long originalStoreId = 200L;
        Long newStoreId = 100L;

        Cart existingCart = new Cart(userId, originalStoreId, new MenuItem(1L, 1));
        given(valueOperations.get("cart:" + userId)).willReturn(objectMapper.writeValueAsString(existingCart));

        MenuItem newItem = new MenuItem(2L, 3);

        // when
        cartRedisRepository.addCartItem(userId, newStoreId, newItem);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, atLeastOnce()).set(eq("cart:" + userId), captor.capture(), any());

        String saved = captor.getValue();
        assertTrue(saved.contains("\"storeId\":100"));
        assertTrue(saved.contains("\"menuId\":2"));
        assertFalse(saved.contains("\"menuId\":1")); // 기존 아이템 없어야 함
    }

    @Test
    @DisplayName("addCartItem 성공 - 같은 가게 + 새로운 메뉴 > 뒤에 붙이기")
    void addCartItem_SameStore_NewMenu() throws JsonProcessingException {
        Long userId = 1L;
        Long storeId = 100L;

        Cart existingCart = new Cart(userId, storeId, new MenuItem(1L, 2)); // 기존 메뉴 있음
        given(valueOperations.get("cart:" + userId)).willReturn(objectMapper.writeValueAsString(existingCart));

        MenuItem newItem = new MenuItem(2L, 1); // 새 메뉴

        cartRedisRepository.addCartItem(userId, storeId, newItem);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, atLeastOnce()).set(eq("cart:" + userId), captor.capture(), any());

        String saved = captor.getValue();
        assertTrue(saved.contains("\"menuId\":1"));
        assertTrue(saved.contains("\"menuId\":2")); // 두 메뉴 다 있어야 함
    }

    @Test
    @DisplayName("addCartItem 성공 - 같은 가게 + 같은 메뉴 > 수량 덮어쓰기")
    void addCartItem_SameStore_SameMenu() throws JsonProcessingException {
        Long userId = 1L;
        Long storeId = 100L;

        MenuItem existing = new MenuItem(1L, 2);
        Cart existingCart = new Cart(userId, storeId, existing);
        given(valueOperations.get("cart:" + userId)).willReturn(objectMapper.writeValueAsString(existingCart));

        MenuItem newItem = new MenuItem(1L, 5); // 같은 메뉴, 수량 변경

        cartRedisRepository.addCartItem(userId, storeId, newItem);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations, atLeastOnce()).set(eq("cart:" + userId), captor.capture(), any());

        String saved = captor.getValue();
        assertTrue(saved.contains("\"menuId\":1"));
        assertTrue(saved.contains("\"quantity\":5")); // 수량이 5로 덮어써졌는지 확인
    }


    @Test
    @DisplayName("getCartData 성공 - 장바구니 불러오기")
    void getCartData_CartExists() throws JsonProcessingException {
        //given
        Long userId = 1L;
        Cart expectedCart = new Cart(userId, 88L, new MenuItem(1L, 1));
        String json = objectMapper.writeValueAsString(expectedCart);

        given(valueOperations.get("cart:" + userId)).willReturn(json);

        //when
        Cart result = cartRedisRepository.getCartData(userId);

        //then
        assertNotNull(result);
        assertEquals(88L, result.getStoreId());
        assertEquals(1, result.getMenus().size());
        assertEquals(1L, result.getMenus().get(0).getMenuId());
    }

    @Test
    @DisplayName("getCartData 성공 - 장바구니없는 경우")
    void getCartData_NotExists() {
        //given
        Long userId = 1L;
        given(valueOperations.get("cart:" + userId)).willReturn(null);

        Cart result = cartRedisRepository.getCartData(userId);

        assertNull(result);
    }

}
