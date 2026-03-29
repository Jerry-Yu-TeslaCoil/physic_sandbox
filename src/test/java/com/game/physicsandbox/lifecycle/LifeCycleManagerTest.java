package com.game.physicsandbox.lifecycle;

import com.game.physicsandbox.MainApplication;
import com.game.physicsandbox.mechanism.UpdateStage;
import com.game.physicsandbox.object.Component;
import com.game.physicsandbox.object.GameObject;
import com.game.physicsandbox.object.GameObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class LifeCycleManagerTest {

    @Autowired
    private LifeCycleManager lifeCycleManager;

    @Autowired
    private GameObjectFactory gameObjectFactory;

    @Test
    public void testComponentRegistrationAndUpdate() {
        // 1. 创建游戏对象
        GameObject gameObject = gameObjectFactory.create("TestObject");
        assertNotNull(gameObject);
        assertEquals("TestObject", gameObject.getName());

        // 2. 创建并添加测试组件
        TestComponent testComponent = new TestComponent();
        gameObject.addComponent(testComponent);

        // 验证组件已添加
        Component[] components = gameObject.getComponents();
        assertEquals(2, components.length); // Transform + TestComponent

        // 3. 延时注册组件到执行器
        lifeCycleManager.slowRegisterToExecutor(testComponent);

        // 4. 执行一次更新（会触发CLEAN阶段的慢注册）
        lifeCycleManager.update(System.nanoTime(), 16_000_000);

        // 5. 验证组件已注册
        // 注意：由于组件已注册，update应该会调用TestComponent的update方法
        // 控制台应该能看到 "Test component update called" 的日志

        // 6. 测试快速注册
        TestComponent quickComponent = new TestComponent();
        gameObject.addComponent(quickComponent);
        lifeCycleManager.quickRegisterToExecutor(quickComponent);

        // 阶段切换时会触发快速注册
        lifeCycleManager.stageSwift(UpdateStage.EVENT);

        // 7. 测试组件注销
        lifeCycleManager.slowUnregisterFromExecutor(testComponent);
        lifeCycleManager.update(System.nanoTime(), 16_000_000);

        // 8. 测试销毁游戏对象
        lifeCycleManager.destroyGameObject(gameObject);
        lifeCycleManager.update(System.nanoTime(), 16_000_000);

        // 验证游戏对象已被移除
        assertTrue(lifeCycleManager.findGameObjects("TestObject").isEmpty());
    }

    @Test
    public void testFindGameObjects() {
        // 创建多个同名对象
        GameObject obj1 = gameObjectFactory.create("SharedName");
        GameObject obj2 = gameObjectFactory.create("SharedName");
        GameObject obj3 = gameObjectFactory.create("DifferentName");

        // 查找同名对象
        var found = lifeCycleManager.findGameObjects("SharedName");
        assertEquals(2, found.size());

        // 清理
        lifeCycleManager.destroyGameObject(obj1);
        lifeCycleManager.destroyGameObject(obj2);
        lifeCycleManager.destroyGameObject(obj3);
        lifeCycleManager.update(System.nanoTime(), 16_000_000);
    }

    @Test
    public void testQuickAndSlowRegistration() {
        GameObject gameObject = gameObjectFactory.create("RegTestObject");
        TestComponent component = new TestComponent();
        gameObject.addComponent(component);

        // 先慢注册
        lifeCycleManager.slowRegisterToExecutor(component);

        // 再快注册（应该会取消慢注册）
        lifeCycleManager.quickRegisterToExecutor(component);

        // 阶段切换时快注册生效，慢注册被取消
        lifeCycleManager.stageSwift(UpdateStage.EVENT);

        // 执行更新
        lifeCycleManager.update(System.nanoTime(), 16_000_000);

        // 清理
        lifeCycleManager.destroyGameObject(gameObject);
        lifeCycleManager.update(System.nanoTime(), 16_000_000);
    }
}