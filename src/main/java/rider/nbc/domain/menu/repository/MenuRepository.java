package rider.nbc.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rider.nbc.domain.menu.entity.Menu;

/**
 * @author    : kimjungmin
 * Created on : 2025. 4. 22.
 */
public interface MenuRepository extends JpaRepository<Menu, Long> {
}