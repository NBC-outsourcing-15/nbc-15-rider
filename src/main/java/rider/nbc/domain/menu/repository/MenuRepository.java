package rider.nbc.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.menu.entity.Menu;
import rider.nbc.domain.menu.exception.MenuException;
import rider.nbc.domain.menu.exception.MenuExceptionCode;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
public interface MenuRepository extends JpaRepository<Menu, Long> {
	default Menu findByIdOrElseThrow(Long menuId) {
		return findById(menuId).orElseThrow(() -> new MenuException(MenuExceptionCode.MENU_NOT_FOUND));
	}
}