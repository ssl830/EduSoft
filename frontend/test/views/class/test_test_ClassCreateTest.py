pytest_plugins = ["pytest_playwright"]

import pytest
import random
from playwright.sync_api import expect

CREATE_URL = "http://localhost:3000/class/create"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_class_create_render(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    expect(page.locator(".class-create-container")).to_be_visible(timeout=10000)
    expect(page.locator("h1.page-title")).to_have_text("创建班级")
    expect(page.locator("select")).to_be_visible()
    expect(page.locator("input[placeholder='例如：0800-0935']")).to_be_visible()
    expect(page.locator("input[placeholder='输入班级唯一代码']")).to_be_visible()
    expect(page.locator(".btn-primary")).to_be_visible()
    context.close()
    browser.close()

def test_class_create_required_fields(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    page.locator(".btn-primary").click()
    expect(page.locator(".error-message")).to_contain_text("请填写所有必填字段")
    context.close()
    browser.close()

def test_class_create_time_format(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    # 选择课程
    select = page.locator("select")
    select.select_option(index=1)
    page.locator("input[placeholder='例如：0800-0935']").fill("800-935")
    page.locator("input[placeholder='输入班级唯一代码']").fill(f"test{random.randint(1000,9999)}")
    page.locator(".btn-primary").click()
    expect(page.locator(".error-message")).to_contain_text("时间格式应为 0800-0935 格式")
    context.close()
    browser.close()

def test_class_create_time_range(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    select = page.locator("select")
    select.select_option(index=1)
    page.locator("input[placeholder='例如：0800-0935']").fill("2500-2600")
    page.locator("input[placeholder='输入班级唯一代码']").fill(f"test{random.randint(1000,9999)}")
    page.locator(".btn-primary").click()
    expect(page.locator(".error-message")).to_contain_text("无效的时间值")
    context.close()
    browser.close()

def test_class_create_start_end(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    select = page.locator("select")
    select.select_option(index=1)
    page.locator("input[placeholder='例如：0800-0935']").fill("0935-0800")
    page.locator("input[placeholder='输入班级唯一代码']").fill(f"test{random.randint(1000,9999)}")
    page.locator(".btn-primary").click()
    expect(page.locator(".error-message")).to_contain_text("开始时间必须早于结束时间")
    context.close()
    browser.close()

def test_class_create_success(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, CREATE_URL)
    select = page.locator("select")
    select.select_option(index=1)
    page.locator("input[placeholder='例如：0800-0935']").fill("0800-0935")
    code = f"test{random.randint(1000,9999)}"
    page.locator("input[placeholder='输入班级唯一代码']").fill(code)
    page.locator(".btn-primary").click()
    expect(page).to_have_url("http://localhost:3000/class")
    context.close()
    browser.close()

