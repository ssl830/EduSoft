pytest_plugins = ["pytest_playwright"]

import pytest
from playwright.sync_api import expect

REGISTER_URL = "http://localhost:3000/register"

def test_register_page_render(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(REGISTER_URL)
    expect(page.locator("h1.auth-title")).to_have_text("注册账号")
    expect(page.locator("input#userId")).to_be_visible()
    expect(page.locator("input#username")).to_be_visible()
    expect(page.locator("input#email")).to_be_visible()
    expect(page.locator("input#password")).to_be_visible()
    expect(page.locator("input#confirmPassword")).to_be_visible()
    expect(page.locator("select#role")).to_be_visible()
    context.close()
    browser.close()

def test_register_input_validation(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(REGISTER_URL)
    page.locator("input#userId").fill("ab")
    page.locator("input#username").fill("a")
    page.locator("input#email").fill("invalidemail")
    page.locator("input#password").fill("123")
    page.locator("input#confirmPassword").fill("456")
    # 注册按钮应为禁用状态
    expect(page.locator("button[type='submit']")).to_be_disabled()
    # 校验提示（分别断言每个错误文本）
    expect(page.get_by_text("用户ID长度必须在3-15个字符之间")).to_be_visible()
    expect(page.get_by_text("用户名长度必须在2-50个字符之间")).to_be_visible()
    expect(page.get_by_text("请输入有效的电子邮箱")).to_be_visible()
    expect(page.get_by_text("密码长度不能少于6个字符")).to_be_visible()
    expect(page.get_by_text("两次输入的密码不一致")).to_be_visible()
    context.close()
    browser.close()

def test_register_success_student(playwright):
    import time
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(REGISTER_URL)
    import random
    uid = f"S{random.randint(1000,9999)}"
    page.locator("input#userId").fill(uid)
    page.locator("input#username").fill("测试学生")
    page.locator("input#email").fill(f"{uid}@test.com")
    page.locator("input#password").fill("123456")
    page.locator("input#confirmPassword").fill("123456")
    page.locator("select#role").select_option("student")
    page.locator("button[type='submit']").click()
    # 注册成功后跳转首页
    expect(page).to_have_url("http://localhost:3000/")
    context.close()
    browser.close()

def test_register_admin_key_modal(playwright):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(REGISTER_URL)
    import random
    uid = f"T{random.randint(1000,9999)}"
    page.locator("input#userId").fill(uid)
    page.locator("input#username").fill("测试管理员")
    page.locator("input#email").fill(f"{uid}@test.com")
    page.locator("input#password").fill("123456")
    page.locator("input#confirmPassword").fill("123456")
    page.locator("select#role").select_option("tutor")
    # 管理员密钥弹窗出现
    expect(page.locator(".modal-mask")).to_be_visible(timeout=3000)
    page.locator(".modal-container input").fill("GuanLiYuanMiYao")
    page.locator(".modal-actions .btn-primary").click()
    # 弹窗消失
    expect(page.locator(".modal-mask")).not_to_be_visible(timeout=3000)
    # 注册成功后跳转首页
    page.locator("button[type='submit']").click()
    expect(page).to_have_url("http://localhost:3000/")
    context.close()
    browser.close()
