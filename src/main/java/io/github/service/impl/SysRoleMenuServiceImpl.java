package io.github.service.impl;

import com.google.common.collect.ImmutableMap;
import io.github.config.aop.service.BaseAopService;
import io.github.dao.SysRoleMenuDao;
import io.github.entity.SysRoleMenuEntity;
import io.github.frame.cache.CacheNameProperty;
import io.github.service.SysRoleMenuService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 角色与菜单对应关系
 *
 * @author Joey
 * @Email 2434387555@qq.com
 */
@Service
public class SysRoleMenuServiceImpl extends BaseAopService<SysRoleMenuServiceImpl, SysRoleMenuDao, SysRoleMenuEntity>
        implements SysRoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long roleId, List<Long> menuIdList) {
        if (menuIdList.size() == 0) {
            return;
        }
        // 先删除角色与菜单关系
        baseMapper.deleteNoMapper(roleId);

        // 保存角色与菜单关系
        Map<String, Object> map = new ImmutableMap.Builder<String, Object>()
                .put("roleId", roleId)
                .put("menuIdList", menuIdList).build();
        // 一定要加上 self 关键字，不然缓存切面不会生效
        self.clearMenuIdList(roleId);
        baseMapper.save(map);
    }

    @Cacheable(value = CacheNameProperty.CACHE_NAME_NORMAL,
            key = "#root.target.cachePrefix + '_' + #roleId")
    @Override
    public List<Long> queryMenuIdList(Long roleId) {
        return baseMapper.queryMenuIdList(roleId);
    }


    /**
     * 清除缓存
     *
     * @param roleId 角色ID
     */
    @CacheEvict(value = CacheNameProperty.CACHE_NAME_NORMAL,
            key = "#root.target.cachePrefix + '_' + #roleId")
    public void clearMenuIdList(Long roleId) {
    }

    @Override
    public String getCachePrefix() {
        return "SYS_ROLE_MENU";
    }
}
