<template>
  <el-dialog
    :close-on-click-modal="false"
    :visible.sync="visible">
      <el-tag
        :key="tag"
        v-for="tag in dynamicTags"
        closable
        :disable-transitions="false"
        @close="handleClose(tag)">
        {{tag.topicName}}
      </el-tag>
      <el-input
        class="input-new-tag"
        v-if="inputVisible"
        v-model="inputValue"
        ref="saveTagInput"
        size="small"
        @keyup.enter.native="handleInputConfirm"
        @blur="handleInputConfirm"
      >
      </el-input>
      <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 新话题</el-button>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>


<script>
  export default {
    data () {
      return {
        visible: false,
        dynamicTags: [],
        inputVisible: false,
        inputValue: '',
        addArray: [],
        deleteArray: [],
        tagId: ''
      }
    },
    methods: {
      init (id) {
        this.visible = true
        this.tagId = id
        this.addArray = []
        this.deleteArray = []
        this.$nextTick(() => {
          this.dynamicTags = []
          this.$http({
            url: this.$http.adornUrl(`/post/tag/queryTopic/${id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dynamicTags = data.topicList
            }
          })
        })
      },
      handleClose (tag) {
        this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1)
        if (tag.id) {
          this.deleteArray.push(tag.id)
        } else {
          this.addArray.splice(this.addArray.indexOf(tag), 1)
        }
        console.log('增加数组的元素', this.addArray)
        console.log('减少数组的元素', this.deleteArray)
      },

      showInput () {
        this.inputVisible = true
        this.$nextTick(_ => {
          this.$refs.saveTagInput.$refs.input.focus()
        })
      },

      handleInputConfirm () {
        let inputValue = this.inputValue
        if (inputValue) {
          let item = {
            'topicName': inputValue
          }
          this.dynamicTags.push(item)
          this.addArray.push(inputValue)
          console.log(this.dynamicTags)
        }
        this.inputVisible = false
        this.inputValue = ''
      },
      // 表单提交
      dataFormSubmit () {
        this.$http({
          url: this.$http.adornUrl(`/post/tag/saveTopic`),
          method: 'post',
          data: this.$http.adornData({
            'tagId': this.tagId || undefined,
            'addArray': this.addArray,
            'deleteArray': this.deleteArray
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.visible = false
                this.$emit('refreshDataList')
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }
    }
  }
</script>


<style>
  .el-tag + .el-tag {
    margin-left: 10px;
  }
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>
