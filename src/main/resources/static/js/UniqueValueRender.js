class UniqueValueRender {

  constructor(map) {
    this.map = map
    this.colorRampList = [
      {
        name: 'ramp1',
        type: 'random',
        colors: []
      },
      {
        name: 'ramp2',
        type: 'diy',
        colors: [
          {
            R: 255,
            G: 0,
            B: 0
          },
          {
            R: 0,
            G: 255,
            B: 0
          },
          {
            R: 0,
            G: 0,
            B: 255
          }
        ]
      }
    ]
  }

  uniqueValueRenderEntry(options) {
    switch (options.layerType) {
      case 'MULTIPOLYGON':
        return this.polygonUniqueValueRender(options)
      case "MULTILINESTRING":
        return this.polylineUniqueValueRender(options)
    }
  }

  polygonUniqueValueRender(options) {
    const { layerId, renderField, uniqueValues, colorRamp, layerOptions } = options
    const layout = layerOptions.layout
    const paint = layerOptions.paint
    const colorRampArray = this.getColorRampArray(colorRamp, uniqueValues.length)
    // console.log(colorRampArray)
    const paintExpressionArr = new Array()
    paintExpressionArr.push('case')
    // paintExpressionArr.push(['get', renderField])
    for (const [index, val] of uniqueValues.entries()) {
      // console.log(index)
      paintExpressionArr.push(['==', ['get', renderField], val])
      paintExpressionArr.push(colorRampArray[index])
    }
    paintExpressionArr.push('#000000')
    console.log(paintExpressionArr)
    Object.keys(layout).forEach(key => {
      this.map.setLayoutProperty(layerId, key, layout[key])
    })
    Object.keys(paint).forEach(key => {
      this.map.setPaintProperty(layerId, key, paint[key])
    })
    this.map.setPaintProperty(layerId, 'fill-color', paintExpressionArr)
  }

  polylineUniqueValueRender(options) {
    const { layerId, renderField, uniqueValues, colorRamp, layerOptions } = options
    const layout = layerOptions.layout
    const paint = layerOptions.paint
    const colorRampArray = this.getColorRampArray(colorRamp, uniqueValues.length)
    // console.log(colorRampArray)
    const paintExpressionArr = new Array()
    paintExpressionArr.push('case')
    // paintExpressionArr.push(['get', renderField])
    for (const [index, val] of uniqueValues.entries()) {
      // console.log(index)
      paintExpressionArr.push(['==', ['get', renderField], val])
      paintExpressionArr.push(colorRampArray[index])
    }
    paintExpressionArr.push('#000000')
    console.log(paintExpressionArr)
    Object.keys(layout).forEach(key => {
      this.map.setLayoutProperty(layerId, key, layout[key])
    })
    Object.keys(paint).forEach(key => {
      this.map.setPaintProperty(layerId, key, paint[key])
    })
    this.map.setPaintProperty(layerId, 'line-color', paintExpressionArr)
  }


  getColorRampArray(rampName, featureCount) {
    const colorRamp = this.colorRampList.find(colorRamp => colorRamp.name === rampName)
    // console.log(colorRamp)
    let colorRampArray = []
    if (colorRamp.type === 'random') {
      for (let i = 0; i < featureCount; i++) {
        colorRampArray.push(this.getRandomColor())
      }
    } else if (colorRamp.type === 'diy') {

    }
    return colorRampArray
  }

  getRandomColor() {
    return '#' + Math.random().toString(16).slice(2, 8)
  }
}

export default UniqueValueRender