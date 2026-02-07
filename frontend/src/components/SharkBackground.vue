<template>
  <div class="ocean-background">
    <div class="shark-wrapper">
      <div class="shark">
        <!-- 修复版极高精度赛博朋克机械鲨鱼 -->
        <svg viewBox="0 0 800 400" xmlns="http://www.w3.org/2000/svg" class="shark-svg">
          <defs>
            <!-- 复杂的光晕滤镜 -->
            <filter id="complex-glow" x="-50%" y="-50%" width="200%" height="200%">
              <feGaussianBlur stdDeviation="2" result="blur"/>
              <feSpecularLighting in="blur" surfaceScale="5" specularConstant=".75" specularExponent="20" lighting-color="#00f3ff" result="specOut">
                <fePointLight x="-5000" y="-10000" z="20000"/>
              </feSpecularLighting>
              <feComposite in="specOut" in2="SourceAlpha" operator="in" result="specOut"/>
              <feComposite in="SourceGraphic" in2="specOut" operator="arithmetic" k1="0" k2="1" k3="1" k4="0" result="litPaint"/>
              <feGaussianBlur stdDeviation="5" in="SourceGraphic" result="glow"/>
              <feMerge>
                <feMergeNode in="glow"/>
                <feMergeNode in="litPaint"/>
              </feMerge>
            </filter>
            
            <!-- 碳纤维纹理 -->
            <pattern id="carbon-fiber" x="0" y="0" width="10" height="10" patternUnits="userSpaceOnUse">
              <rect width="10" height="10" fill="#0a1a2a"/>
              <path d="M0,10 L10,0 M-2,2 L2,-2 M8,12 L12,8" stroke="#1a2a3a" stroke-width="1"/>
            </pattern>

            <!-- 能量核心渐变 -->
            <radialGradient id="core-energy" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
              <stop offset="0%" stop-color="#fff" stop-opacity="1" />
              <stop offset="40%" stop-color="#00f3ff" stop-opacity="1" />
              <stop offset="100%" stop-color="#00f3ff" stop-opacity="0" />
            </radialGradient>

            <!-- 扫描线 -->
            <linearGradient id="scan-line" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stop-color="#00f3ff" stop-opacity="0" />
              <stop offset="50%" stop-color="#00f3ff" stop-opacity="0.5" />
              <stop offset="100%" stop-color="#00f3ff" stop-opacity="0" />
            </linearGradient>
          </defs>

          <g class="shark-container" filter="url(#complex-glow)">
            
            <!-- === 尾部推进器系统 === -->
            <!-- 使用嵌套组：外层定位，内层动画 -->
            <g transform="translate(50, 200)">
              <g class="thruster-anim">
                  <!-- 主喷口 -->
                  <path d="M0,-20 L60,-30 L60,30 L0,20 Z" fill="#112233" stroke="#ff00de" stroke-width="2" />
                  <rect x="50" y="-25" width="10" height="50" fill="#223344" stroke="#00f3ff" stroke-width="1" />
                  
                  <!-- 辅助喷口 -->
                  <path d="M20,-35 L50,-45 L50,-25 L20,-25 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />
                  <path d="M20,25 L50,25 L50,45 L20,35 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />

                  <!-- 粒子火焰 -->
                  <path d="M-10,0 L-80,-25 L-60,0 L-80,25 Z" fill="url(#core-energy)" opacity="0.8">
                    <animate attributeName="d" values="M-10,0 L-80,-25 L-60,0 L-80,25 Z; M-10,0 L-100,-30 L-70,0 L-100,30 Z; M-10,0 L-80,-25 L-60,0 L-80,25 Z" dur="0.08s" repeatCount="indefinite" />
                  </path>
              </g>
            </g>

            <!-- === 身体主体 === -->
            <g transform="translate(120, 200)">
              <g class="body-anim">
                  <!-- 内部骨架 -->
                  <path d="M20,0 L380,0" stroke="#1a3a4a" stroke-width="10" stroke-linecap="round" />
                  
                  <!-- 主装甲板 -->
                  <path d="M0,0 C60,-60 180,-70 300,-40 C360,-30 420,0 450,20 C420,60 300,80 180,70 C100,60 30,30 0,0 Z" fill="url(#carbon-fiber)" stroke="#00f3ff" stroke-width="2" />
                  
                  <!-- 机械细节：散热片 -->
                  <g transform="translate(100, -50)">
                    <path d="M0,0 L20,-10 L40,0 L20,10 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                    <path d="M50,5 L70,-5 L90,5 L70,15 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                    <path d="M100,10 L120,0 L140,10 L120,20 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="1" />
                  </g>

                  <!-- 能量核心 -->
                  <circle cx="250" cy="10" r="15" fill="url(#core-energy)">
                    <animate attributeName="r" values="15;18;15" dur="2s" repeatCount="indefinite" />
                    <animate attributeName="opacity" values="0.8;1;0.8" dur="2s" repeatCount="indefinite" />
                  </circle>
                  
                  <!-- 能量管路 -->
                  <path d="M265,10 L350,10 L400,20" fill="none" stroke="#00f3ff" stroke-width="2" stroke-dasharray="5,5">
                     <animate attributeName="stroke-dashoffset" from="100" to="0" dur="1s" repeatCount="indefinite" />
                  </path>
                  <path d="M235,10 L150,10 L100,0" fill="none" stroke="#00f3ff" stroke-width="2" stroke-dasharray="5,5">
                     <animate attributeName="stroke-dashoffset" from="0" to="100" dur="1s" repeatCount="indefinite" />
                  </path>

                  <!-- 侧面装甲接缝 -->
                  <path d="M150,-60 L150,60 M250,-45 L250,65 M350,-25 L350,45" stroke="#00f3ff" stroke-width="1" opacity="0.3" />
                  
                  <!-- 机械鳃 (动态开合) -->
                  <g transform="translate(280, -15)">
                    <path d="M0,0 L10,0" stroke="#00f3ff" stroke-width="4">
                      <animate attributeName="d" values="M0,0 L10,0; M0,0 L10,5; M0,0 L10,0" dur="1s" repeatCount="indefinite" />
                    </path>
                    <path d="M15,0 L25,0" stroke="#00f3ff" stroke-width="4">
                      <animate attributeName="d" values="M15,0 L25,0; M15,0 L25,5; M15,0 L25,0" dur="1s" begin="0.1s" repeatCount="indefinite" />
                    </path>
                    <path d="M30,0 L40,0" stroke="#00f3ff" stroke-width="4">
                      <animate attributeName="d" values="M30,0 L40,0; M30,0 L40,5; M30,0 L40,0" dur="1s" begin="0.2s" repeatCount="indefinite" />
                    </path>
                  </g>

                  <!-- 背鳍 (带天线) -->
                  <g transform="translate(220, -65)">
                    <path d="M0,0 L40,-80 L80,10 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                    <line x1="40" y1="-80" x2="20" y2="-100" stroke="#ff00de" stroke-width="1" />
                    <circle cx="20" cy="-100" r="2" fill="#ff00de">
                      <animate attributeName="opacity" values="1;0;1" dur="0.5s" repeatCount="indefinite" />
                    </circle>
                  </g>
                  
                  <!-- 腹鳍 -->
                  <path d="M220,65 L240,110 L280,75 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                  
                  <!-- 全息数据投影 -->
                  <g transform="translate(250, -80)" opacity="0.6">
                     <rect x="0" y="0" width="60" height="20" fill="none" stroke="#00f3ff" stroke-width="0.5" />
                     <path d="M5,10 L55,10" stroke="#00f3ff" stroke-width="0.5" stroke-dasharray="2,2" />
                     <text x="5" y="15" fill="#00f3ff" font-size="8" font-family="monospace">SYS:OK</text>
                  </g>
              </g>
            </g>

            <!-- === 头部 === -->
            <!-- 使用嵌套组：外层定位，内层动画 -->
            <g transform="translate(570, 220)">
              <g class="head-anim">
                  <!-- 头部装甲 -->
                  <path d="M0,0 C40,-15 80,-10 120,10 C80,40 40,35 0,25 Z" fill="#0a1a2a" stroke="#00f3ff" stroke-width="2" />
                  
                  <!-- 机械下颚 -->
                  <path d="M20,25 L100,25 L80,40 L20,35 Z" fill="#112233" stroke="#00f3ff" stroke-width="1" />
                  
                  <!-- 眼睛 (多重结构) -->
                  <g transform="translate(60, 10)">
                    <circle cx="0" cy="0" r="8" fill="#000" stroke="#ff0000" stroke-width="1" />
                    <circle cx="0" cy="0" r="4" fill="#ff0000">
                      <animate attributeName="opacity" values="1;0.5;1" dur="0.2s" repeatCount="indefinite" />
                    </circle>
                    <!-- 扫描光束 -->
                    <path d="M0,0 L150,-60 L150,60 Z" fill="url(#scan-line)" opacity="0.3">
                      <animateTransform attributeName="transform" type="rotate" values="-10 0 0; 10 0 0; -10 0 0" dur="2s" repeatCount="indefinite" />
                    </path>
                  </g>

                  <!-- 牙齿 (激光刃) -->
                  <path d="M30,25 L40,30 L50,25 L60,30 L70,25" fill="none" stroke="#fff" stroke-width="2" filter="url(#complex-glow)" />
              </g>
            </g>
            
            <!-- === 尾鳍 === -->
            <!-- 使用嵌套组：外层定位，内层动画 -->
            <g transform="translate(120, 200)">
               <g class="tail-anim">
                   <path d="M0,0 C-40,-50 -80,-100 -60,-130 C-30,-100 0,-50 10,-10 C0,30 -30,80 -60,110 C-80,80 -40,30 0,0 Z" fill="url(#carbon-fiber)" stroke="#00f3ff" stroke-width="2" />
                   <!-- 尾鳍加强筋 -->
                   <path d="M-20,-60 L-10,-20 M-20,60 L-10,20" stroke="#00f3ff" stroke-width="1" />
               </g>
            </g>
          </g>
        </svg>
      </div>
    </div>
    <div class="bubbles">
      <span v-for="n in 10" :key="n" :style="{ '--i': n }"></span>
    </div>
  </div>
</template>

<script setup>
// 纯展示组件，无逻辑
</script>

<style scoped>
/* 海洋背景动画层 */
.ocean-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
  background-color: #0B0E11; /* 统一深色背景 */
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  background-position: center;
  overflow: hidden;
}

/* 添加暗角效果 */
.ocean-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, transparent 30%, rgba(11, 14, 17, 0.6) 100%);
  pointer-events: none;
  z-index: 1;
}

.shark-wrapper {
  position: absolute;
  top: 50%;
  left: -50%; /* 初始位置在屏幕左侧外 */
  width: 65vw;
  height: 32.5vw;
  transform: translateY(-50%);
  animation: swim 30s linear infinite;
  opacity: 0.8;
  z-index: 0;
}

.shark {
  width: 100%;
  height: 100%;
  animation: float 6s ease-in-out infinite;
}

/* 鲨鱼游动动画 */
.shark-svg {
  width: 100%;
  height: 100%;
  overflow: visible;
}

.head-anim {
  transform-origin: 0 0; /* 围绕连接点旋转 */
  animation: head-swing 4s ease-in-out infinite alternate;
}

.tail-anim {
  transform-origin: 0 0; /* 围绕连接点旋转 */
  animation: tail-swing 3s ease-in-out infinite alternate;
}

/* 推进器火焰闪烁 */
.thruster-anim path {
  animation: flicker 0.1s infinite alternate;
}

@keyframes head-swing {
  from { transform: rotate(3deg); }
  to { transform: rotate(-3deg); }
}

@keyframes tail-swing {
  from { transform: rotate(8deg); }
  to { transform: rotate(-8deg); }
}

@keyframes swim {
  0% {
    left: -60%;
    transform: translateY(-50%) scaleX(1); /* 向右游 */
  }
  45% {
    left: 120%;
    transform: translateY(-50%) scaleX(1);
  }
  50% {
    left: 120%;
    transform: translateY(-50%) scaleX(-1); /* 翻转，向左游 */
  }
  95% {
    left: -60%;
    transform: translateY(-50%) scaleX(-1);
  }
  100% {
    left: -60%;
    transform: translateY(-50%) scaleX(1); /* 恢复 */
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-30px); }
}

@keyframes flicker {
  0% { opacity: 0.8; }
  100% { opacity: 0.4; }
}

/* 气泡动画 */
.bubbles {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.bubbles span {
  position: absolute;
  bottom: -20px;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: rise 10s infinite ease-in;
  animation-delay: calc(var(--i) * 1s);
  left: calc(var(--i) * 10%);
}

@keyframes rise {
  0% {
    bottom: -20px;
    transform: translateX(0);
  }
  50% {
    transform: translateX(20px);
  }
  100% {
    bottom: 100%;
    transform: translateX(-20px);
  }
}
</style>