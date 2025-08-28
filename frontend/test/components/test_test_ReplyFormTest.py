pytest_plugins = ["pytest_playwright"]

import pytest
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/discussions/1"
LOGIN_URL = "http://localhost:3000/login"
USER_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url("http://localhost:3000/")
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_replyform_basic_flow(playwright):
    browser, context, page = login_and_get_page(playwright, USER_CREDENTIALS, BASE_URL)
    # 等待讨论区加载
    expect(page.locator(".reply-form")).to_be_visible(timeout=10000)
    textarea = page.locator(".reply-form textarea")
    # 输入内容
    textarea.fill("测试回复内容")
    expect(textarea).to_have_value("测试回复内容")
    # 字数统计
    expect(page.locator(".reply-form .text-xs.text-gray-400")).to_contain_text("6/2000")
    # 格式化按钮
    page.locator(".wb-btn[title='粗体']").click()
    expect(textarea).to_have_value("测试回复内容****")
    # 插入表情
    page.locator(".wb-btn[title='表情']").click()
    page.locator(".grid.grid-cols-8 button:has-text('😀')").first.click()
    expect(textarea).to_have_value("测试回复内容**😀**")
    # 超长输入
    long_text = "a" * 2100
    textarea.fill(long_text)
    expect(page.locator(".reply-form .text-red-600")).to_be_visible()
    expect(page.locator(".reply-form .text-red-600")).to_contain_text("内容长度不能超过")
    # 清空并尝试提交
    textarea.fill("")
    # 提交按钮应为禁用状态
    expect(page.locator(".reply-form .submit-button")).to_be_disabled()
    # 填写内容再提交
    textarea.fill("自动化测试回复")
    page.locator(".reply-form .submit-button").click()
    # 提交后内容清空
    expect(textarea).to_have_value("")
    # 取消按钮
    textarea.fill("取消测试")
    page.locator(".reply-form .cancel-button").click()
    expect(textarea).to_have_value("")
    context.close()
    browser.close()
