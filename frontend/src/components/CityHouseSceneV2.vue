<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import {
  AmbientLight,
  BoxGeometry,
  BufferGeometry,
  Clock,
  Color,
  CylinderGeometry,
  DirectionalLight,
  DodecahedronGeometry,
  Fog,
  Group,
  InstancedMesh,
  MathUtils,
  Mesh,
  MeshStandardMaterial,
  Object3D,
  PCFSoftShadowMap,
  PerspectiveCamera,
  Scene,
  SphereGeometry,
  SRGBColorSpace,
  Vector3,
  WebGLRenderer,
} from 'three'

type WindowTone = 'blue' | 'cyan' | 'navy'
type RoofStyle = 'room' | 'tank' | 'antenna'

interface BuildingSpec {
  x: number
  z: number
  width: number
  height: number
  depth: number
  color: string
  windows: WindowTone
  floors: number
  columns: number
  bands?: boolean
  balconies?: boolean
  roof?: RoofStyle
}

const host = ref<HTMLDivElement>()
const canvas = ref<HTMLCanvasElement>()
const failed = ref(false)
const viewMode = ref<'district' | 'residence'>('district')
const buildingCount = ref(0)
const windowCount = ref(0)

let cleanup: (() => void) | undefined

function toggleView() {
  viewMode.value = viewMode.value === 'district' ? 'residence' : 'district'
}

onMounted(() => {
  const hostElement = host.value
  const canvasElement = canvas.value
  if (!hostElement || !canvasElement) return

  try {
    const scene = new Scene()
    scene.fog = new Fog(new Color('#b9dfe2'), 33, 62)

    const camera = new PerspectiveCamera(32, 1, 0.1, 100)
    camera.position.set(22, 16, 27)
    camera.lookAt(0, 3.8, 0)

    const renderer = new WebGLRenderer({
      canvas: canvasElement,
      alpha: true,
      antialias: true,
      powerPreference: 'high-performance',
    })
    renderer.outputColorSpace = SRGBColorSpace
    renderer.shadowMap.enabled = true
    renderer.shadowMap.type = PCFSoftShadowMap
    renderer.setClearColor(0xffffff, 0)

    const geometries = new Set<BufferGeometry>()
    const materials = new Set<MeshStandardMaterial>()

    const palette = {
      cream: '#f3efe4',
      white: '#fffdf5',
      cobalt: '#3157e1',
      cyan: '#10a8b8',
      coral: '#ed6545',
      orange: '#d94a2f',
      plum: '#713d79',
      purple: '#8a4fc5',
      wine: '#9b385a',
      yellow: '#e2b83d',
      navy: '#172238',
      green: '#23946c',
      asphalt: '#536075',
    }

    function createMaterial(
      color: string,
      options: { roughness?: number; metalness?: number; emissive?: string } = {},
    ) {
      const next = new MeshStandardMaterial({
        color,
        roughness: options.roughness ?? 0.78,
        metalness: options.metalness ?? 0.01,
        emissive: options.emissive ?? '#000000',
        emissiveIntensity: options.emissive ? 0.16 : 0,
      })
      materials.add(next)
      return next
    }

    function makeBox(width: number, height: number, depth: number, color: string, castShadow = true) {
      const geometry = new BoxGeometry(width, height, depth)
      geometries.add(geometry)
      const mesh = new Mesh(geometry, createMaterial(color))
      mesh.castShadow = castShadow
      mesh.receiveShadow = true
      return mesh
    }

    const city = new Group()
    city.rotation.y = -0.31
    scene.add(city)

    const base = makeBox(29, 0.35, 21, '#c6d5d4')
    base.position.y = -0.2
    city.add(base)

    const blockPads = [
      [-8.8, -5.2, 9.4, 7.2, '#e8dcc6'],
      [7.3, -5.4, 9.2, 7.1, '#d8e7df'],
      [-8.6, 4.4, 9.6, 7.4, '#e7d8d0'],
      [6.7, 4.3, 10.2, 7.4, '#e8e2c9'],
    ] as const

    for (const [x, z, width, depth, color] of blockPads) {
      const pad = makeBox(width, 0.12, depth, color, false)
      pad.position.set(x, 0.02, z)
      city.add(pad)
    }

    const roadMaterial = createMaterial(palette.asphalt, { roughness: 0.93 })
    const roadSpecs = [
      [0, 0.02, 0, 2.15, 0.08, 21.1],
      [0, 0.025, 0, 29.1, 0.08, 1.85],
      [0, 0.03, 7.9, 29.1, 0.07, 0.9],
    ] as const
    for (const [x, y, z, width, height, depth] of roadSpecs) {
      const geometry = new BoxGeometry(width, height, depth)
      geometries.add(geometry)
      const road = new Mesh(geometry, roadMaterial)
      road.position.set(x, y, z)
      road.receiveShadow = true
      city.add(road)
    }

    const lineMaterial = createMaterial('#f4d45e', { roughness: 0.85 })
    const lineGeometry = new BoxGeometry(0.08, 0.02, 1.1)
    geometries.add(lineGeometry)
    for (let z = -9; z <= 9; z += 2.1) {
      const line = new Mesh(lineGeometry, lineMaterial)
      line.position.set(0, 0.08, z)
      city.add(line)
    }

    const crosswalkGeometry = new BoxGeometry(0.23, 0.025, 1.45)
    geometries.add(crosswalkGeometry)
    const crosswalkMaterial = createMaterial('#f6f2e7', { roughness: 0.9 })
    for (let x = -1.4; x <= 1.4; x += 0.42) {
      const stripe = new Mesh(crosswalkGeometry, crosswalkMaterial)
      stripe.position.set(x, 0.09, 2.4)
      stripe.rotation.y = Math.PI / 2
      city.add(stripe)
    }

    const windowMaterials = {
      blue: createMaterial(palette.cobalt, { roughness: 0.32, metalness: 0.08, emissive: '#162963' }),
      cyan: createMaterial(palette.cyan, { roughness: 0.32, metalness: 0.08, emissive: '#0c3c46' }),
      navy: createMaterial(palette.navy, { roughness: 0.38, metalness: 0.04 }),
    }

    function addWindows(target: Group, spec: BuildingSpec) {
      const sideColumns = Math.max(2, Math.floor(spec.columns * 0.48))
      const total = spec.floors * (spec.columns + sideColumns)
      windowCount.value += total
      const windowWidth = Math.min(0.44, (spec.width * 0.67) / spec.columns)
      const windowHeight = Math.min(0.34, (spec.height * 0.5) / spec.floors)
      const geometry = new BoxGeometry(windowWidth, windowHeight, 0.075)
      geometries.add(geometry)
      const windows = new InstancedMesh(geometry, windowMaterials[spec.windows], total)
      const dummy = new Object3D()
      let index = 0

      for (let row = 0; row < spec.floors; row += 1) {
        const y = 0.58 + row * ((spec.height - 1.1) / Math.max(1, spec.floors - 1))
        for (let column = 0; column < spec.columns; column += 1) {
          const x = -spec.width * 0.34 + column * ((spec.width * 0.68) / Math.max(1, spec.columns - 1))
          dummy.position.set(x, y, spec.depth / 2 + 0.045)
          dummy.rotation.set(0, 0, 0)
          dummy.updateMatrix()
          windows.setMatrixAt(index, dummy.matrix)
          index += 1
        }
        for (let column = 0; column < sideColumns; column += 1) {
          const z = -spec.depth * 0.28 + column * ((spec.depth * 0.56) / Math.max(1, sideColumns - 1))
          dummy.position.set(spec.width / 2 + 0.045, y, z)
          dummy.rotation.set(0, Math.PI / 2, 0)
          dummy.updateMatrix()
          windows.setMatrixAt(index, dummy.matrix)
          index += 1
        }
      }

      windows.instanceMatrix.needsUpdate = true
      target.add(windows)
    }

    function addRoof(target: Group, spec: BuildingSpec) {
      const roofRoom = makeBox(spec.width * 0.42, 0.38, spec.depth * 0.44, '#d6d8d1')
      roofRoom.position.y = spec.height + 0.19
      target.add(roofRoom)

      if (spec.roof === 'tank') {
        const geometry = new CylinderGeometry(0.2, 0.2, 0.58, 10)
        geometries.add(geometry)
        const tank = new Mesh(geometry, createMaterial(palette.navy))
        tank.position.set(spec.width * 0.18, spec.height + 0.68, 0)
        tank.castShadow = true
        target.add(tank)
      }

      if (spec.roof === 'antenna') {
        const geometry = new CylinderGeometry(0.025, 0.04, 1.65, 8)
        geometries.add(geometry)
        const antenna = new Mesh(geometry, createMaterial('#465166'))
        antenna.position.set(0, spec.height + 1.08, 0)
        target.add(antenna)
        const marker = makeBox(0.18, 0.18, 0.18, palette.coral)
        marker.position.set(0, spec.height + 1.9, 0)
        target.add(marker)
      }
    }

    function createBuilding(spec: BuildingSpec) {
      const building = new Group()
      const podium = makeBox(spec.width + 0.24, 0.32, spec.depth + 0.24, '#e5e2d8')
      podium.position.y = 0.16
      building.add(podium)

      const body = makeBox(spec.width, spec.height, spec.depth, spec.color)
      body.position.y = spec.height / 2 + 0.28
      building.add(body)
      addWindows(building, { ...spec, height: spec.height + 0.28 })

      if (spec.bands) {
        const bandMaterial = createMaterial('#f5f1e8', { roughness: 0.86 })
        const bandGeometry = new BoxGeometry(spec.width + 0.12, 0.08, spec.depth + 0.12)
        geometries.add(bandGeometry)
        for (let floor = 2; floor < spec.floors; floor += 3) {
          const band = new Mesh(bandGeometry, bandMaterial)
          band.position.y = 0.4 + floor * (spec.height / spec.floors)
          building.add(band)
        }
      }

      if (spec.balconies) {
        for (let floor = 2; floor < spec.floors; floor += 2) {
          const slab = makeBox(spec.width * 0.62, 0.08, 0.42, '#fffdf6')
          slab.position.set(-spec.width * 0.08, 0.38 + floor * (spec.height / spec.floors), spec.depth / 2 + 0.22)
          building.add(slab)
          const rail = makeBox(spec.width * 0.58, 0.18, 0.035, palette.cobalt)
          rail.position.set(-spec.width * 0.08, slab.position.y + 0.16, spec.depth / 2 + 0.41)
          building.add(rail)
        }
      }

      addRoof(building, spec)
      building.position.set(spec.x, 0, spec.z)
      city.add(building)
    }

    const buildingSpecs: BuildingSpec[] = [
      { x: -12.3, z: -7.8, width: 2.3, height: 8.8, depth: 2.2, color: palette.plum, windows: 'cyan', floors: 10, columns: 4, roof: 'antenna' },
      { x: -9.6, z: -7.7, width: 2.7, height: 10.2, depth: 2.3, color: palette.cream, windows: 'navy', floors: 12, columns: 5, bands: true, roof: 'tank' },
      { x: -6.3, z: -7.6, width: 2.8, height: 7.4, depth: 2.2, color: palette.coral, windows: 'navy', floors: 9, columns: 5, balconies: true },
      { x: -3.1, z: -7.6, width: 2.7, height: 9.5, depth: 2.3, color: palette.white, windows: 'blue', floors: 11, columns: 5, bands: true },
      { x: 3.1, z: -7.6, width: 2.8, height: 10.8, depth: 2.4, color: palette.cream, windows: 'blue', floors: 13, columns: 5, bands: true, roof: 'antenna' },
      { x: 6.4, z: -7.7, width: 2.8, height: 7.8, depth: 2.2, color: palette.wine, windows: 'cyan', floors: 9, columns: 5, balconies: true },
      { x: 9.6, z: -7.8, width: 2.6, height: 11.4, depth: 2.3, color: palette.yellow, windows: 'navy', floors: 13, columns: 5, bands: true },
      { x: 12.3, z: -7.7, width: 2.1, height: 8.2, depth: 2.2, color: palette.cobalt, windows: 'cyan', floors: 10, columns: 4, roof: 'tank' },
      { x: -12.1, z: -3.9, width: 2.5, height: 6.2, depth: 2.4, color: palette.orange, windows: 'navy', floors: 7, columns: 4, balconies: true },
      { x: -9.1, z: -3.8, width: 2.7, height: 7.8, depth: 2.4, color: palette.white, windows: 'blue', floors: 9, columns: 5, bands: true },
      { x: -5.8, z: -3.7, width: 2.8, height: 5.9, depth: 2.4, color: palette.purple, windows: 'cyan', floors: 7, columns: 5 },
      { x: 5.7, z: -3.8, width: 2.9, height: 6.7, depth: 2.4, color: palette.coral, windows: 'navy', floors: 8, columns: 5, balconies: true },
      { x: 9, z: -3.8, width: 2.7, height: 8.5, depth: 2.4, color: palette.cream, windows: 'blue', floors: 10, columns: 5, bands: true, roof: 'tank' },
      { x: 12, z: -3.8, width: 2.4, height: 6.3, depth: 2.3, color: palette.cyan, windows: 'navy', floors: 7, columns: 4 },
      { x: -12, z: 2.2, width: 2.6, height: 6.9, depth: 2.5, color: palette.cream, windows: 'blue', floors: 8, columns: 5, bands: true },
      { x: -8.8, z: 2.4, width: 2.8, height: 5.4, depth: 2.5, color: palette.wine, windows: 'cyan', floors: 6, columns: 5, balconies: true },
      { x: -5.6, z: 2.4, width: 2.7, height: 7.6, depth: 2.4, color: palette.yellow, windows: 'navy', floors: 9, columns: 5, roof: 'antenna' },
      { x: 8.7, z: 2.5, width: 2.9, height: 6.8, depth: 2.6, color: palette.cobalt, windows: 'cyan', floors: 8, columns: 5, balconies: true },
      { x: 12, z: 2.3, width: 2.5, height: 5.6, depth: 2.5, color: palette.coral, windows: 'navy', floors: 6, columns: 4 },
      { x: -11.7, z: 6.4, width: 2.8, height: 4.8, depth: 2.4, color: palette.cobalt, windows: 'cyan', floors: 5, columns: 5 },
      { x: -8.4, z: 6.5, width: 2.8, height: 6.2, depth: 2.4, color: palette.white, windows: 'blue', floors: 7, columns: 5, bands: true },
      { x: -5.1, z: 6.4, width: 2.7, height: 4.6, depth: 2.5, color: palette.orange, windows: 'navy', floors: 5, columns: 5, balconies: true },
      { x: 5, z: 6.4, width: 2.8, height: 5.1, depth: 2.5, color: palette.plum, windows: 'cyan', floors: 6, columns: 5 },
      { x: 8.3, z: 6.5, width: 2.8, height: 6.6, depth: 2.4, color: palette.cream, windows: 'blue', floors: 8, columns: 5, bands: true },
      { x: 11.5, z: 6.4, width: 2.6, height: 4.9, depth: 2.4, color: palette.cyan, windows: 'navy', floors: 5, columns: 4 },
    ]

    buildingSpecs.forEach(createBuilding)
    buildingCount.value = buildingSpecs.length + 1

    function createResidence() {
      const residence = new Group()
      residence.position.set(0, 0, 2.3)

      const plaza = makeBox(8.4, 0.15, 6.6, '#f0d14e')
      plaza.position.y = 0.08
      residence.add(plaza)

      const podium = makeBox(7.5, 0.62, 5.5, palette.cobalt)
      podium.position.y = 0.42
      residence.add(podium)

      const groundFloor = makeBox(6.8, 1.35, 4.8, palette.white)
      groundFloor.position.set(0, 1.38, 0)
      residence.add(groundFloor)

      const coralWing = makeBox(2.25, 2.3, 4.25, palette.coral)
      coralWing.position.set(-2.05, 2.72, -0.16)
      residence.add(coralWing)

      const cyanWing = makeBox(3.55, 1.85, 3.72, palette.cyan)
      cyanWing.position.set(1.3, 2.5, -0.35)
      residence.add(cyanWing)

      const upperHouse = makeBox(3.8, 1.5, 3.05, palette.cream)
      upperHouse.position.set(-0.35, 4.25, -0.56)
      residence.add(upperHouse)

      for (const [width, y, depth] of [[7.35, 2.08, 5.12], [6.1, 3.5, 4.32], [4.25, 5.02, 3.38]] as const) {
        const slab = makeBox(width, 0.12, depth, '#fffdf5')
        slab.position.set(0, y, -0.18)
        residence.add(slab)
      }

      const glassBands = [
        [-1.35, 1.43, 2.43, 3.2, palette.navy],
        [1.25, 2.55, 1.54, 2.4, palette.cobalt],
        [-0.34, 4.28, 1.01, 2.55, palette.cyan],
      ] as const
      for (const [x, y, z, width, color] of glassBands) {
        const glass = makeBox(width, 0.62, 0.08, color)
        glass.position.set(x, y, z)
        residence.add(glass)
      }

      for (let index = 0; index < 7; index += 1) {
        const column = makeBox(0.1, 1.05, 0.1, '#fffdf5')
        column.position.set(-2.75 + index * 0.92, 1.55, 2.48)
        residence.add(column)
      }

      for (const x of [-2.7, 2.72]) {
        const balcony = makeBox(1.35, 0.1, 0.72, '#fffdf5')
        balcony.position.set(x, 3.45, 1.65)
        residence.add(balcony)
        const rail = makeBox(1.25, 0.25, 0.04, palette.navy)
        rail.position.set(x, 3.66, 1.98)
        residence.add(rail)
      }

      const roofRoom = makeBox(1.15, 0.58, 0.9, palette.purple)
      roofRoom.position.set(0.65, 5.36, -0.65)
      residence.add(roofRoom)

      for (let index = 0; index < 4; index += 1) {
        const beam = makeBox(0.1, 0.85, 0.1, palette.coral)
        beam.position.set(-1.55 + index * 0.72, 5.5, 0.52)
        residence.add(beam)
      }
      const pergola = makeBox(2.35, 0.1, 0.12, palette.coral)
      pergola.position.set(-0.48, 5.91, 0.52)
      residence.add(pergola)

      const pool = makeBox(2.4, 0.12, 1.1, '#23bfd0')
      pool.position.set(1.55, 2.2, 1.35)
      residence.add(pool)

      city.add(residence)
      return residence
    }

    const residence = createResidence()

    const treeGeometry = new DodecahedronGeometry(0.3, 0)
    geometries.add(treeGeometry)
    const treeMaterial = createMaterial(palette.green, { roughness: 0.92 })
    const treePositions = [
      [-3.8, 4.6], [3.9, 4.7], [-3.8, 0.3], [3.8, 0.4], [-13.4, 0.9], [-10.8, 0.9], [-7.6, 0.9],
      [7.1, 0.9], [10.2, 0.9], [13.3, 0.9], [-13.4, 8.7], [-9.6, 8.7], [8.9, 8.7], [12.9, 8.7],
    ] as const
    for (const [x, z] of treePositions) {
      const trunk = makeBox(0.09, 0.55, 0.09, '#75533e')
      trunk.position.set(x, 0.36, z)
      city.add(trunk)
      const crown = new Mesh(treeGeometry, treeMaterial)
      crown.position.set(x, 0.92, z)
      crown.castShadow = true
      city.add(crown)
    }

    const lampGeometry = new CylinderGeometry(0.025, 0.035, 0.8, 8)
    const lampHeadGeometry = new SphereGeometry(0.1, 8, 6)
    geometries.add(lampGeometry)
    geometries.add(lampHeadGeometry)
    const lampMaterial = createMaterial(palette.navy)
    const lampGlow = createMaterial('#f6d45a', { emissive: '#f6d45a', roughness: 0.5 })
    for (let z = -8.5; z <= 8.5; z += 2.8) {
      for (const x of [-1.35, 1.35]) {
        const pole = new Mesh(lampGeometry, lampMaterial)
        pole.position.set(x, 0.46, z)
        city.add(pole)
        const light = new Mesh(lampHeadGeometry, lampGlow)
        light.position.set(x, 0.91, z)
        city.add(light)
      }
    }

    const carColors = [palette.coral, palette.yellow, palette.cobalt, palette.cyan, palette.purple, palette.white]
    const carPositions = [
      [-0.62, -6.7, 0], [0.65, -3.2, Math.PI], [-0.62, 5.8, 0], [0.62, 8.8, Math.PI], [-8.5, 7.9, Math.PI / 2], [7.4, 7.9, Math.PI / 2],
    ] as const
    carPositions.forEach(([x, z, rotation], index) => {
      const car = new Group()
      const body = makeBox(0.56, 0.2, 1.05, carColors[index])
      body.position.y = 0.2
      car.add(body)
      const cabin = makeBox(0.44, 0.18, 0.48, palette.navy)
      cabin.position.set(0, 0.36, -0.04)
      car.add(cabin)
      car.position.set(x, 0, z)
      car.rotation.y = rotation
      city.add(car)
    })

    scene.add(new AmbientLight('#ffffff', 1.35))

    const sun = new DirectionalLight('#fff2d4', 3.5)
    sun.position.set(13, 24, 17)
    sun.castShadow = true
    sun.shadow.mapSize.set(1024, 1024)
    sun.shadow.camera.left = -20
    sun.shadow.camera.right = 20
    sun.shadow.camera.top = 20
    sun.shadow.camera.bottom = -20
    scene.add(sun)

    const fill = new DirectionalLight('#78c8ff', 1.8)
    fill.position.set(-18, 10, 4)
    scene.add(fill)

    const clock = new Clock()
    const districtCamera = new Vector3(22, 16, 27)
    const residenceCamera = new Vector3(12.5, 8.8, 15.5)
    const desiredCamera = new Vector3()
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)')

    let frame = 0
    let pointerX = 0
    let pointerY = 0
    let dragYaw = 0
    let dragging = false
    let previousX = 0

    const resize = () => {
      const width = Math.max(1, hostElement.clientWidth)
      const height = Math.max(1, hostElement.clientHeight)
      renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.65))
      renderer.setSize(width, height, false)
      camera.aspect = width / height
      camera.updateProjectionMatrix()
    }

    const pointerMove = (event: PointerEvent) => {
      const rect = hostElement.getBoundingClientRect()
      pointerX = ((event.clientX - rect.left) / rect.width - 0.5) * 2
      pointerY = ((event.clientY - rect.top) / rect.height - 0.5) * 2
      if (dragging) {
        dragYaw += (event.clientX - previousX) * 0.004
        previousX = event.clientX
      }
    }

    const pointerDown = (event: PointerEvent) => {
      if ((event.target as HTMLElement).closest('button')) return
      dragging = true
      previousX = event.clientX
      hostElement.classList.add('is-dragging')
      hostElement.setPointerCapture(event.pointerId)
    }

    const pointerUp = (event: PointerEvent) => {
      dragging = false
      hostElement.classList.remove('is-dragging')
      if (hostElement.hasPointerCapture(event.pointerId)) hostElement.releasePointerCapture(event.pointerId)
    }

    const pointerLeave = () => {
      if (!dragging) {
        pointerX = 0
        pointerY = 0
      }
    }

    const render = () => {
      const time = clock.getElapsedTime()
      const basePosition = viewMode.value === 'district' ? districtCamera : residenceCamera
      desiredCamera.copy(basePosition)
      desiredCamera.x += pointerX * (viewMode.value === 'district' ? 1.2 : 0.7)
      desiredCamera.y -= pointerY * 0.45
      camera.position.lerp(desiredCamera, 0.045)

      const idleYaw = reduceMotion.matches ? 0 : Math.sin(time * 0.17) * 0.03
      city.rotation.y = MathUtils.lerp(city.rotation.y, -0.31 + dragYaw + pointerX * 0.045 + idleYaw, 0.045)
      city.rotation.x = MathUtils.lerp(city.rotation.x, pointerY * 0.012, 0.045)
      residence.position.y = reduceMotion.matches ? 0 : Math.sin(time * 0.8) * 0.045
      camera.lookAt(0, viewMode.value === 'district' ? 3.75 : 2.7, 0.8)
      renderer.render(scene, camera)
      frame = window.requestAnimationFrame(render)
    }

    const resizeObserver = new ResizeObserver(resize)
    resizeObserver.observe(hostElement)
    hostElement.addEventListener('pointermove', pointerMove)
    hostElement.addEventListener('pointerdown', pointerDown)
    hostElement.addEventListener('pointerup', pointerUp)
    hostElement.addEventListener('pointercancel', pointerUp)
    hostElement.addEventListener('pointerleave', pointerLeave)
    resize()
    render()

    cleanup = () => {
      window.cancelAnimationFrame(frame)
      resizeObserver.disconnect()
      hostElement.removeEventListener('pointermove', pointerMove)
      hostElement.removeEventListener('pointerdown', pointerDown)
      hostElement.removeEventListener('pointerup', pointerUp)
      hostElement.removeEventListener('pointercancel', pointerUp)
      hostElement.removeEventListener('pointerleave', pointerLeave)
      geometries.forEach((geometry) => geometry.dispose())
      materials.forEach((item) => item.dispose())
      renderer.dispose()
    }
  } catch (error) {
    console.error('Three.js 城市场景初始化失败', error)
    failed.value = true
  }
})

onBeforeUnmount(() => cleanup?.())
</script>

<template>
  <div ref="host" class="city-house-scene city-house-scene-v2">
    <canvas
      ref="canvas"
      class="city-house-canvas"
      role="img"
      aria-label="包含现代住宅、高层建筑、道路、车辆、绿化和街灯的可旋转三维城市模型"
    />
    <button class="scene-view-toggle" type="button" @click="toggleView">
      <span>{{ viewMode === 'district' ? '聚焦住宅' : '查看全城' }}</span>
      <i>{{ viewMode === 'district' ? '01 / 02' : '02 / 02' }}</i>
    </button>
    <div class="scene-model-stats">
      <span><strong>{{ buildingCount }}</strong> BUILDINGS</span>
      <span><strong>{{ windowCount }}</strong> WINDOWS</span>
    </div>
    <div v-if="failed" class="scene-fallback">
      <strong>3D 场景暂不可用</strong>
      <span>业务功能仍可正常浏览</span>
    </div>
    <div class="scene-coordinate scene-coordinate-left">31.2304° N / 121.4737° E</div>
    <div class="scene-coordinate scene-coordinate-right">DRAG TO ROTATE</div>
  </div>
</template>
