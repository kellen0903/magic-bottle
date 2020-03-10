<div class="app-container">
  <!--工具栏-->
  <div class="head-container">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-date-picker
        v-model="dataForm.startTime"
        type="datetime"
        @input="logTimeChangeStart"
        placeholder="开始时间">
        value-format="yyyy-MM-dd HH:mm:ss"
      </el-date-picker>
      <el-date-picker
        v-model="dataForm.endTime"
        type="datetime"
        @input="logTimeChangeEnd"
        placeholder="结束时间">
        value-format="yyyy-MM-dd HH:mm:ss"
      </el-date-picker>
      <el-form-item>
        <el-input v-model="dataForm.toUid" placeholder="被举报人Id" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.num" placeholder="被举报次数" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        prop="userId"
        header-align="center"
        align="center"
        label="被举报人id">
      </el-table-column>
      <el-table-column
        prop="count"
        header-align="center"
        align="center"
        label="被举报次数">
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>
  import moment from 'moment'

  export default {
    data () {
      return {
        dataForm: {
          key: '',
          toUid: '',
          fromUid: '',
          itemType: '',
          startTime: '',
          endTime: '',
          num: 2
        },
        options: [
          {
            itemType: 1,
            itemName: '帖子'
          },
          {
            itemType: 2,
            itemName: '评论'
          },
          {
            itemType: 3,
            itemName: '会话'
          }
        ],
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: []
      }
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      logTimeChangeStart (time) {
        console.log(time)
        if (time) {
          this.dataForm.startTime = moment(time).format('YYYY-MM-DD HH:mm:ss')
        }
      },
      logTimeChangeEnd (time) {
        if (time) {
          this.dataForm.endTime = moment(time).format('YYYY-MM-DD HH:mm:ss')
        }
      },
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/support/tipofflog/report'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'startTime': this.dataForm.startTime,
            'endTime': this.dataForm.endTime,
            'toUid': this.dataForm.toUid,
            'num': this.dataForm.num
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/support/tipofflog/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      }
    }
  }
</script>
