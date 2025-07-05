
// 增强的烟花效果
function createFirework(x, y) {
    console.log('创建烟花在位置:', x, y);

    const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff'];
    const particleCount = 35; // 增加粒子数量

    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'firework-particle';

        // 设置更大的粒子样式
        const color = colors[Math.floor(Math.random() * colors.length)];
        particle.style.backgroundColor = color;
        particle.style.boxShadow = `0 0 15px ${color}, 0 0 30px ${color}`;

        // 设置初始位置
        particle.style.left = (x - 6) + 'px';
        particle.style.top = (y - 6) + 'px';

        document.body.appendChild(particle);

        // 计算运动方向 - 增加扩散范围
        const angle = (Math.PI * 2 * i) / particleCount;
        const speed = 4 + Math.random() * 6; // 增加速度
        let dx = Math.cos(angle) * speed;
        let dy = Math.sin(angle) * speed;
        let opacity = 1;
        let size = 8; // 初始大小更大

        // 动画函数
        function animate() {
            const currentLeft = parseFloat(particle.style.left);
            const currentTop = parseFloat(particle.style.top);

            particle.style.left = (currentLeft + dx) + 'px';
            particle.style.top = (currentTop + dy) + 'px';

            // 添加重力效果
            dy += 0.15;
            dx *= 0.98;

            // 更慢的淡出效果
            opacity -= 0.015;
            size *= 0.985;

            particle.style.opacity = opacity;
            particle.style.width = size + 'px';
            particle.style.height = size + 'px';

            if (opacity > 0 && size > 1) {
                requestAnimationFrame(animate);
            } else {
                if (particle.parentNode) {
                    particle.parentNode.removeChild(particle);
                }
            }
        }

        // 延迟启动动画，创造爆炸效果
        setTimeout(() => {
            requestAnimationFrame(animate);
        }, Math.random() * 150);
    }
}

// 随机位置创建烟花
function createRandomFirework() {
    const x = Math.random() * window.innerWidth;
    const y = Math.random() * window.innerHeight;
    createFirework(x, y);
    console.log('随机烟花触发'); // 调试用
}

// 点击页面创建烟花
document.addEventListener('click', function(e) {
    // 排除按钮和表单元素
    if (e.target.tagName !== 'BUTTON' &&
        e.target.tagName !== 'INPUT' &&
        e.target.tagName !== 'LABEL') {
        createFirework(e.clientX, e.clientY);
        console.log('点击烟花触发'); // 调试用
    }
});

// 页面加载完成后先来一个烟花
window.addEventListener('load', function() {
    console.log('页面加载完成，准备显示烟花');
    setTimeout(() => {
        createFirework(window.innerWidth / 2, window.innerHeight / 2);
    }, 1000);
});