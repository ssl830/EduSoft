<template>
  <div>
    <textarea v-model="content" class="rich-textarea" placeholder="请输入内容..."></textarea>
  </div>
</template>

<script>
export default {
  name: 'RichTextEditor',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  data() {
    return {
      content: this.modelValue
    };
  },
  watch: {
    content(newValue) {
      this.$emit('update:modelValue', newValue);
    }
  }
};
</script>

<style scoped>
.rich-textarea {
  width: 100%;
  min-height: 200px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
}
</style>
