# 主要结构

- ### 机制结构
  - 物理分析器PhysicAnalyzer
    - 物理分析层
    - 碰撞检测层
    - 碰撞约束处理层
    - 约束求解层
      - 顺序脉冲法
    - 物理更新层
  - 事件总线EventBus
  - 画面渲染器Renderer
  - 输入控制器Controller
  - 帧调度管理器FrameManager

- ### 每帧阶段处理
  - 用户输入监听
  - 事件分发
  - 物理分析
    - 元素合冲量计算
    - 碰撞检测
    - 碰撞约束生成
    - 约束求解
    - 物理更新
  - 画面渲染

- ### 对象结构
  - 元素Element定义容器
    - addComponent(Component component): 添加组件
    - removeComponent(Component component): 移除组件
    - update(): 执行对象更新
  - 组件Component定义行为
    - update(): 执行组件更新，添加力、位移，发布事件，渲染画面，监听输入
    - setActivate(boolean isActivate): 设置是否生效
    - setUpdateLayer(): 设置更新阶段