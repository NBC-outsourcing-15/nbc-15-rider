package rider.nbc.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rider.nbc.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu,Long> {
}
