<template>
  <div class="mod-demo-echarts mod-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div id="userBox" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="24" class="text-center">
        <el-button type="primary" @click="userChartsClick(1)">天</el-button>
        <el-button type="primary" @click="userChartsClick(2)">周</el-button>
        <el-button type="primary" @click="userChartsClick(3)">月</el-button>
        <el-button type="primary" @click="userChartsClick(4)">年</el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div id="cardBox" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="24" class="text-center">
        <el-button type="primary" @click="cardChartsClick(1)">天</el-button>
        <el-button type="primary" @click="cardChartsClick(2)">周</el-button>
        <el-button type="primary" @click="cardChartsClick(3)">月</el-button>
        <el-button type="primary" @click="cardChartsClick(4)">年</el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div id="dialogBox" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="24" class="text-center">
        <el-button type="primary" @click="dialogChartsClick(1)">天</el-button>
        <el-button type="primary" @click="dialogChartsClick(2)">周</el-button>
        <el-button type="primary" @click="dialogChartsClick(3)">月</el-button>
        <el-button type="primary" @click="dialogChartsClick(4)">年</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import echarts from 'echarts'
  export default {
    data () {
      return {
        userChartLine: null,
        cardChartLine: null,
        dialogChartLine: null,
        userData: [],
        cardData: [],
        dialogData: [],
        userCountType: 1,
        cardCountType: 1,
        dialogCountType: 1
      }
    },
    activated () {
      // 由于给echart添加了resize事件, 在组件激活时需要重新resize绘画一次, 否则出现空白bug
      if (this.userChartLine) {
        this.userChartLine.resize()
      }
      if (this.cardChartLine) {
        this.cardChartLine.resize()
      }
      if (this.dialogChartLine) {
        this.dialogChartLine.resize()
      }
    },
    mounted () {
      this.initUserData(this.userCountType)
      this.initCardData(this.cardCountType)
      this.initDialogData(this.dialogCountType)
    },
    methods: {
      userChartsClick (type) {
        this.initUserData(type)
      },
      cardChartsClick (type) {
        this.initCardData(type)
      },
      dialogChartsClick (type) {
        this.initDialogData(type)
      },
      initUserData (type) {
        this.$http({
          url: this.$http.adornUrl(`/count/user/list?countType=${type}`),
          method: 'get'
        }).then(({ data }) => {
          this.userData = data
          this.initUserChartLine()
        })
      },
      initCardData (type) {
        this.$http({
          url: this.$http.adornUrl(`/count/post/list?countType=${type}`),
          method: 'get'
        }).then(({ data }) => {
          this.cardData = data
          this.initCardChartLine()
        })
      },
      initDialogData (type) {
        this.$http({
          url: this.$http.adornUrl(`/count/chat/list?countType=${type}`),
          method: 'get'
        }).then(({ data }) => {
          this.dialogData = data
          this.initDialogChartLine()
        })
      },
      initUserChartLine () {
        let countArray = []
        let dateArray = []
        let maleArray = []
        let femaleArray = []
        let otherArray = []
        this.userData.data.map(data => {
          for (let keys of Object.keys(data)) {
            if (keys === 'count') {
              countArray.push(data.count)
            } else if (keys === 'male') {
              maleArray.push(data[keys])
            } else if (keys === 'female') {
              femaleArray.push(data[keys])
            } else if (keys === 'other') {
              otherArray.push(data[keys])
            } else {
              dateArray.push(data[keys])
            }
          }
        })
        var option = {
          'title': {
            'text': '用户'
          },
          'tooltip': {
            'trigger': 'axis',
            'axisPointer': {
              'type': 'shadow'
            }
          },
          'legend': {
            'data': ['所有用户', '男', '女', '其它']
          },
          'grid': {
            'left': '3%',
            'right': '4%',
            'bottom': '3%',
            'containLabel': true
          },
          'toolbox': {
            'feature': {
              'saveAsImage': {}
            }
          },
          'xAxis': {
            'type': 'category',
            'boundaryGap': false,
            'data': dateArray
          },
          'yAxis': {
            'type': 'value'
          },
          'series': [
            {
              'name': '所有用户',
              'type': 'bar',
              'stack': '总量',
              'itemStyle': {
                'normal': {
                  'color': 'rgb(255, 219, 92)'
                }
              },
              'data': countArray
            },
            {
              'name': '男',
              'type': 'bar',
              'stack': '总量',
              'itemStyle': {
                'normal': {
                  'color': 'rgb(50, 197, 233)'
                }
              },
              'data': maleArray
            },
            {
              'name': '女',
              'type': 'bar',
              'stack': '总量',
              'itemStyle': {
                'normal': {
                  'color': 'rgb(251, 114, 147)'
                }
              },
              'data': femaleArray
            },
            {
              'name': '其它',
              'type': 'bar',
              'stack': '总量',
              'itemStyle': {
                'normal': {
                  'color': 'rgb(159, 230, 184)'
                }
              },
              'data': otherArray
            }
          ]
        }
        this.userChartLine = echarts.init(document.getElementById('userBox'))
        this.userChartLine.setOption(option)
        window.addEventListener('resize', () => {
          this.userChartLine.resize()
        })
      },
      initCardChartLine () {
        let countArray = []
        let dateArray = []
        this.cardData.data.map(data => {
          for (let keys of Object.keys(data)) {
            if (keys === 'count') {
              countArray.push(data.count)
            } else {
              dateArray.push(data[keys])
            }
          }
        })
        var option = {
          'title': {
            'text': '帖子'
          },
          'tooltip': {
            'trigger': 'axis'
          },
          'grid': {
            'left': '3%',
            'right': '4%',
            'bottom': '3%',
            'containLabel': true
          },
          'toolbox': {
            'feature': {
              'saveAsImage': {}
            }
          },
          'xAxis': {
            'type': 'category',
            'boundaryGap': false,
            'data': dateArray
          },
          'yAxis': {
            'type': 'value'
          },
          'series': [
            {
              'name': '搜索引擎',
              'type': 'line',
              'stack': '总量',
              'data': countArray
            }
          ]
        }
        this.cardChartLine = echarts.init(document.getElementById('cardBox'))
        this.cardChartLine.setOption(option)
        window.addEventListener('resize', () => {
          this.cardChartLine.resize()
        })
      },
      initDialogChartLine () {
        let countArray = []
        let dateArray = []
        this.dialogData.data.map(data => {
          for (let keys of Object.keys(data)) {
            if (keys === 'count') {
              countArray.push(data.count)
            } else {
              dateArray.push(data[keys])
            }
          }
        })
        var option = {
          'title': {
            'text': '会话'
          },
          'tooltip': {
            'trigger': 'axis'
          },
          'grid': {
            'left': '3%',
            'right': '4%',
            'bottom': '3%',
            'containLabel': true
          },
          'toolbox': {
            'feature': {
              'saveAsImage': {}
            }
          },
          'xAxis': {
            'type': 'category',
            'boundaryGap': false,
            'data': dateArray
          },
          'yAxis': {
            'type': 'value'
          },
          'series': [
            {
              'name': '搜索引擎',
              'type': 'line',
              'stack': '总量',
              'data': countArray
            }
          ]
        }
        this.dialogChartLine = echarts.init(document.getElementById('dialogBox'))
        this.dialogChartLine.setOption(option)
        window.addEventListener('resize', () => {
          this.dialogChartLine.resize()
        })
      }
    }
  }
</script>

<style lang="scss">
  .mod-demo-echarts {
    > .el-alert {
      margin-bottom: 10px;
    }
    > .el-row {
      margin-top: -10px;
      margin-bottom: -10px;
      .el-col {
        padding-top: 10px;
        padding-bottom: 10px;
      }
    }
    .chart-box {
      min-height: 400px;
    }
  }

  .text-center {
    text-align: center;
  }
</style>
